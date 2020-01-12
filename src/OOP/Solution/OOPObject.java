package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class OOPObject {
    //Mandatory fields
    private List<Object> directParents;
    private Map<String, Object> virtualAncestor;
    //Static fields for virtual ancestors and stuff
    private static Map<String, Object> virtual_objects;
    private static Class<?> most_derived_class = null;

    static private void dfs_construct (Class<?> c, List<Class<?>> virtualBaseClasses) {
        //DFS Rules!!
        for (OOPParent anot : c.getAnnotationsByType(OOPParent.class)) {
            dfs_construct(anot.parent(), virtualBaseClasses);
            if (anot.isVirtual() && !virtualBaseClasses.contains(c)){
                virtualBaseClasses.add(anot.parent());
            }
        }
    }

    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        directParents = new LinkedList<Object>();
        virtualAncestor = new HashMap<String, Object>();

        if (null == most_derived_class) {
            virtual_objects = new HashMap<String, Object>();
            most_derived_class = this.getClass();
        }
        //Take care of virtual base classes
        LinkedList<Class<?>> virtuals = new LinkedList<Class<?>>();
        OOPParent[] lst = this.getClass().getAnnotationsByType(OOPParent.class);
        dfs_construct(this.getClass(), virtuals);

        try {
            //Phase 1 - create new instances of new virtual ancestors if they are not in the virtualObjects map yet
            List <Class<?>> toConstruct = virtuals.stream().filter(virt -> !virtual_objects.containsKey(virt.getName())).collect(Collectors.toCollection(LinkedList::new));

            for (Class<?> c : toConstruct) {
                if (virtual_objects.containsKey(c.getName())) {
                    continue;
                }
                Constructor<?> m = c.getDeclaredConstructor();
                if (Modifier.isPrivate(m.getModifiers())) {
                    throw new OOP4ObjectInstantiationFailedException ();
                } else {
                    m.setAccessible(true);
                }
                Object inst = m.newInstance();
                virtual_objects.put(c.getName(), inst);
            }
            //Phase 2 - update virtual ancestor list according to virtual objects map
            virtual_objects.values().stream().map(obj -> virtualAncestor.put(obj.getClass().getName(), obj));
            //Phase 3 - go on all annotations as usual and add to direct parents
            for (OOPParent anot : lst) {
                if (anot.isVirtual()) {
                    directParents.add(virtual_objects.get(anot.parent().getName()));
                } else {
                    Constructor<?> m = anot.parent().getDeclaredConstructor();
                    if (!Modifier.isPrivate(m.getModifiers())) {
                        m.setAccessible(true);
                    } else {
                        throw new OOP4ObjectInstantiationFailedException ();
                    }
                    Object inst = m.newInstance();
                    directParents.add(inst);
                }
            }
            //Phase 4 - undo the most derived if this is the same class
            if (this.getClass() == most_derived_class) {
                most_derived_class = null;
            }
        }
        catch (OOP4ObjectInstantiationFailedException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            //Dont forget to delete most derived as construction failed
            most_derived_class = null;
            throw new OOP4ObjectInstantiationFailedException();
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
        if (cls == OOPObject.class) {
            return false;
        }
        if (this.getClass() == cls) {
            return true;
        }
        boolean flag = directParents.stream().filter(Obj -> Obj instanceof OOPObject).map(Obj -> (OOPObject) Obj).anyMatch(Obj -> Obj.multInheritsFrom(cls));
        if (flag) {
            return true;
        }
        return directParents.stream().anyMatch(cls::isInstance);
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        try {
            Method m = this.getClass().getDeclaredMethod(methodName, argTypes);
            if (!Modifier.isPrivate(m.getModifiers())) {
                return this;
            }
        } catch (NoSuchMethodException ignored) {        }
        Set<Object> definers = new HashSet<Object>(); //TODO: check if can be done by array and not set
        for (Object parent : directParents) {
            if (!(parent instanceof OOPObject)) {
                try {
                    parent.getClass().getMethod(methodName, argTypes);
                    definers.add(parent);
                } catch (NoSuchMethodException ignored) { }
            } else {
                try {
                    Object o = (((OOPObject) parent).definingObject(methodName, argTypes));
                    definers.add(o);
                } catch ( OOP4NoSuchMethodException ignored) { }
            }
        }
        if (definers.size() == 0) {
            throw new  OOP4NoSuchMethodException ();
        } else if (definers.size()>1) {
            throw new OOP4AmbiguousMethodException ();
        }
        return definers.toArray()[0];
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        List<Class<?>> argTypes = Arrays.stream(callArgs).map(Object::getClass).collect(Collectors.toCollection(LinkedList::new));
        try {
            Object definer = definingObject(methodName, argTypes.toArray(new Class[argTypes.size()]));
            Method m = definer.getClass().getMethod(methodName, argTypes.toArray(new Class[argTypes.size()]));
            return m.invoke(definer, callArgs);
        } catch (NoSuchMethodException e) {
            throw new OOP4NoSuchMethodException ();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw  new OOP4MethodInvocationFailedException ();
        } //Rest of the OOP4 Exceptions are thrown anyway from this function
    }
}

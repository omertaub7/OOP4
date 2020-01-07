package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.io.ObjectStreamException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class OOPObject {
    List<Object> directParents;
    Map<String, Class<?>> virtualAncestor;

    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        directParents = new LinkedList<Object>();
        virtualAncestor = new HashMap<String, Class<?>>();
        OOPParent[] lst = this.getClass().getAnnotationsByType(OOPParent.class);
        List<Class<?>> classes = Arrays.stream(lst).map(OOPParent::parent).collect(Collectors.toList());
        //TODO: Take care of virtual Ancestors
        for (Class<?> c : classes) {
            try {
                Constructor<?> construct = c.getConstructor();
                Object [] initArgs = new Object[0];
                Object o =  construct.newInstance(initArgs);
                directParents.add(o);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new OOP4ObjectInstantiationFailedException();
            }
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
        if (this.getClass() == cls) {
            return true;
        }
        boolean flag = directParents.stream().filter(Obj -> Obj instanceof OOPObject).map(Obj -> (OOPObject) Obj).anyMatch(Obj -> Obj.multInheritsFrom(cls));
        if (flag) {
            return true;
        }
        return directParents.stream().anyMatch(cls::isInstance);
    }
    private static Object getDefiner(Object obj, String methodName, Class<?>... argTypes) {
        if (obj.getClass() == Object.class) {
            return obj;
        }
        try {
            Method m = (obj.getClass().getDeclaredMethod(methodName, argTypes));
            return obj;
        } catch (NoSuchMethodException e) {
            return getDefiner(obj.getClass(), methodName, argTypes);
        }
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        //List<Object> lst = directParents.stream().filter(obj -> Arrays.stream(obj.getClass().getMethods()).map(Method::getName).anyMatch(mth -> mth.equals(methodName))).collect(Collectors.toList());
        //Case 1 - only self declaring a method, and no one else in the tree
        //if (lst.size() == 0) {
        try {
            this.getClass().getDeclaredMethod(methodName, argTypes);
            return this;
        } catch (NoSuchMethodException ignored) {        }
        //}
        Set<Object> definers = new HashSet<Object>(); //TODO: check if can be done by array and not set
        for (Object parent : directParents) {
            if (!(parent instanceof OOPObject)) {
                try {
                    parent.getClass().getMethod(methodName, argTypes);
                    Object o = getDefiner(parent, methodName, argTypes);
                    definers.add(o);
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
        return null;
    }
}

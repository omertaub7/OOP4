package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.io.ObjectStreamException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        // TODO: Implement
        return null;
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        // TODO: Implement
        return null;
    }
}

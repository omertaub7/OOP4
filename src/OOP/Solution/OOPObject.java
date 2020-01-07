package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class OOPObject {
    List<Object> directParents;
    Map<String, Class<?>> virtualAncestor;

    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        directParents = new LinkedList<Class<?>>();
        virtualAncestor = new HashMap<String, Class<?>>();
        OOPParent[] lst = this.getClass().getAnnotationsByType(OOPParent.class);
        List<Class<?>> classes = Arrays.stream(lst).map(OOPParent::parent).collect(Collectors.toList());
        //TODO: Take care of virtual Ancestors
        for (Class<?> c : classes) {



            }
        }

    }

    public boolean multInheritsFrom(Class<?> cls) {
        // TODO: Implement
        return false;
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

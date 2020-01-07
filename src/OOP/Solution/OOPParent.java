package OOP.Solution;

import java.lang.annotation.Repeatable;
import java.lang.annotation.*;
import java.util.*;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;

@Repeatable(OOPParents.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, CONSTRUCTOR})
public @interface OOPParent {
    Class<?> parent ();
    boolean isVirtual = false;
}
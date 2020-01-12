package OOP.Solution;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Repeatable(OOPParents.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface OOPParent {
    Class<?> parent ();
    boolean isVirtual () default false;
}
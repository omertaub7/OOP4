package OOP.Solution;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface OOPParents {
    // This is the container annotation for OOPParent.
    // Modify it to comply with the requirements for the @Repeatable annotation.
    OOPParent[] value();
}

package OOP.Solution;

import java.lang.annotation.Repeatable;
import java.lang.annotation.*;
import java.util.*;

import static java.lang.annotation.ElementType.TYPE;

@Repeatable(value = OOPParent.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE})
public @interface OOPParent {
    // TODO: Implement
}

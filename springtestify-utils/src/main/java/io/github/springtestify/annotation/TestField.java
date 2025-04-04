package io.github.springtestify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be automatically populated with test data.
 * Can be used with specific generator annotations or will use default generators based on field type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TestField {
    /**
     * Optional name of the generator to use
     */
    String generator() default "";

    /**
     * Optional static value to use instead of generating
     */
    String value() default "";
}

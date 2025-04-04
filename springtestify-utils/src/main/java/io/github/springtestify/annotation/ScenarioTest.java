package io.github.springtestify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class as a scenario test and configures it automatically.
 * Eliminates the need for constructor boilerplate.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ScenarioTest {
    /**
     * The entity class to test
     */
    Class<?> value();

    /**
     * The base path for the API endpoints. If not specified,
     * will be derived from the entity class name.
     */
    String path() default "";
}

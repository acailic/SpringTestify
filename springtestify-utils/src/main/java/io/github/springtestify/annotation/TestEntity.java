package io.github.springtestify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures test entity generation at the class level.
 * Allows defining default values, field generators, and test scenarios.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TestEntity {
    /**
     * Define test scenarios with specific field values
     */
    Scenario[] scenarios() default {};

    /**
     * Default ID value for test entities
     */
    String defaultId() default "1";

    /**
     * Whether to automatically generate ID for test entities
     */
    boolean autoGenerateId() default true;

    /**
     * Whether to validate entity constraints during test entity creation
     */
    boolean validateConstraints() default true;

    /**
     * Annotation to define a test scenario
     */
    @interface Scenario {
        /**
         * Name of the scenario
         */
        String name();

        /**
         * Field values for this scenario
         */
        FieldValue[] values();
    }

    /**
     * Annotation to define a field value in a scenario
     */
    @interface FieldValue {
        /**
         * Name of the field
         */
        String field();

        /**
         * Value to set for the field
         */
        String value();
    }
}

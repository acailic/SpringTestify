package io.github.springtestify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method to use a specific test scenario.
 * Links test methods with entity scenarios defined in @TestEntity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TestScenario {
    /**
     * Name of the scenario to use from the entity's @TestEntity annotation
     */
    String value();

    /**
     * Description of what this scenario is testing
     */
    String description() default "";

    /**
     * Test expectations
     */
    Expect[] expect() default {};

    /**
     * Additional field overrides for this specific test
     */
    TestEntity.FieldValue[] overrides() default {};

    /**
     * Whether to skip common entity matchers for this test
     */
    boolean skipCommonMatchers() default false;
}

package io.github.springtestify.annotation;

import org.springframework.http.HttpStatus;
import java.lang.annotation.*;

/**
 * Defines expectations for a test scenario.
 * Can be used within @TestScenario or standalone.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Repeatable(Expect.List.class)
public @interface Expect {
    /**
     * HTTP status to expect
     */
    HttpStatus status() default HttpStatus.OK;

    /**
     * JSON paths to expect in the response
     */
    String[] jsonPath() default {};

    /**
     * Expected values for JSON paths
     */
    String[] value() default {};

    /**
     * Fields that should exist in the response
     */
    String[] exists() default {};

    /**
     * Fields that should not exist in the response
     */
    String[] notExists() default {};

    /**
     * Fields that should be null in the response
     */
    String[] isNull() default {};

    /**
     * Fields that should not be null in the response
     */
    String[] notNull() default {};

    /**
     * Error message to expect (if any)
     */
    String error() default "";

    /**
     * Container annotation for repeatable @Expect
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface List {
        Expect[] value();
    }
}

package io.github.springtestify.annotation;

import org.springframework.http.HttpMethod;
import java.lang.annotation.*;

/**
 * Defines the action to perform in a test scenario.
 * When used with @TestScenario, automatically handles test execution.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ScenarioAction {
    /**
     * HTTP method to use for the request
     */
    HttpMethod method() default HttpMethod.POST;

    /**
     * Additional path to append to the base path
     */
    String path() default "";

    /**
     * Whether to include request body
     */
    boolean includeBody() default true;

    /**
     * Custom request parameters
     */
    RequestParam[] params() default {};

    /**
     * Custom request headers
     */
    RequestHeader[] headers() default {};

    /**
     * Define a request parameter
     */
    @interface RequestParam {
        String name();
        String value();
    }

    /**
     * Define a request header
     */
    @interface RequestHeader {
        String name();
        String value();
    }
}

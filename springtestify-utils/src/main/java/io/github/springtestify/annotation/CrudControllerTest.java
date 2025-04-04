package io.github.springtestify.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Annotation for CRUD controller tests.
 * Combines Spring Boot test configuration with custom CRUD test settings.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public @interface CrudControllerTest {
    /**
     * Base path for all CRUD operations
     */
    @AliasFor("path")
    String value() default "";

    /**
     * Base path for all CRUD operations
     */
    @AliasFor("value")
    String path() default "";

    /**
     * Whether to include authentication in requests
     */
    boolean requiresAuth() default false;

    /**
     * Whether to validate responses against OpenAPI schema
     */
    boolean validateSchema() default true;

    /**
     * Whether to include common entity matchers in all tests
     */
    boolean includeCommonMatchers() default true;
}

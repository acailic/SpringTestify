package io.github.springtestify.core.annotation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for testing Spring service classes.
 * <p>
 * This annotation is a meta-annotation that configures a test for a service class
 * with automatic mocking of dependencies.
 * <p>
 * Example usage:
 * <pre>
 * &#064;ServiceTest
 * public class UserServiceTest {
 *     // Test methods
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
public @interface ServiceTest {
    /**
     * Alias for {@link SpringBootTest#classes}.
     * <p>
     * Specifies the classes to include in the test context.
     * @return the classes to include
     */
    @AliasFor(annotation = SpringBootTest.class, attribute = "classes")
    Class<?>[] classes() default {};

    /**
     * The service class under test.
     * <p>
     * This can be used to automatically configure the test context.
     * @return the service class under test
     */
    Class<?> service() default void.class;

    /**
     * Whether to automatically mock dependencies of the service.
     * @return true if dependencies should be automatically mocked, false otherwise
     */
    boolean mockDependencies() default true;
}

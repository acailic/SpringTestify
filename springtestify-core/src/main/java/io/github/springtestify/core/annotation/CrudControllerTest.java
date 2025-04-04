package io.github.springtestify.core.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for testing CRUD controllers.
 * <p>
 * This annotation is a meta-annotation that configures a test for a CRUD controller
 * with automatic mocking of dependencies.
 * <p>
 * Example usage:
 * <pre>
 * &#064;CrudControllerTest
 * public class UserControllerTest extends AbstractCrudControllerTest&lt;User&gt; {
 *     // Test methods
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@AutoConfigureMockMvc
public @interface CrudControllerTest {
    /**
     * Alias for {@link SpringBootTest#classes}.
     * <p>
     * Specifies the classes to include in the test context.
     * @return the classes to include
     */
    @AliasFor(annotation = SpringBootTest.class, attribute = "classes")
    Class<?>[] classes() default {};

    /**
     * The entity class for the CRUD operations.
     * <p>
     * This can be used to automatically configure the test context.
     * @return the entity class
     */
    Class<?> value() default void.class;

    /**
     * The base path for the CRUD operations.
     * <p>
     * This is used to construct the URLs for the CRUD operations.
     * @return the base path
     */
    String basePath() default "";

    /**
     * Whether to automatically mock dependencies of the controller.
     * @return true if dependencies should be automatically mocked, false otherwise
     */
    boolean mockDependencies() default true;
}

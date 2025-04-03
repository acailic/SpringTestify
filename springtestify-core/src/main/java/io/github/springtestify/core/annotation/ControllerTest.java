package io.github.springtestify.core.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for testing Spring MVC controllers.
 * <p>
 * This annotation is a meta-annotation that combines Spring's {@link WebMvcTest}
 * with additional configuration to simplify controller testing.
 * <p>
 * Example usage:
 * <pre>
 * &#064;ControllerTest(path = "/api/users")
 * public class UserControllerTest {
 *     // Test methods
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@WebMvcTest
public @interface ControllerTest {
    /**
     * Alias for {@link WebMvcTest#controllers}.
     * <p>
     * Specifies the controllers to test.
     * @return the controllers to test
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
    
    /**
     * The API path associated with the controller under test.
     * <p>
     * This can be used to automatically configure request paths in tests.
     * @return the API path
     */
    String path() default "";
    
    /**
     * Whether to apply Spring Security autoconfiguration.
     * @return true if Spring Security should be autoconfigured, false otherwise
     */
    boolean withSecurity() default false;
}
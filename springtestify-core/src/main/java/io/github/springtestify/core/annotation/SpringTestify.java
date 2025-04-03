package io.github.springtestify.core.annotation;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

/**
 * Main annotation for SpringTestify that combines multiple Spring testing annotations
 * to provide a comprehensive testing environment.
 * <p>
 * This annotation is a meta-annotation that combines various Spring testing annotations
 * to simplify the setup of tests for Spring applications.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DataJpaTest
@ActiveProfiles("test")
public @interface SpringTestify {
    /**
     * Alias for {@link ActiveProfiles#profiles}.
     * <p>
     * Specifies which active profiles should be used.
     * @return the active profiles to use
     */
    @AliasFor(annotation = ActiveProfiles.class, attribute = "profiles")
    String[] activeProfiles() default {"test"};

    /**
     * Determines if the test context should be cached.
     * <p>
     * By default, the test context is cached to improve performance.
     * @return true if the test context should be cached, false otherwise
     */
    boolean cacheTestContext() default true;
}

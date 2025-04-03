package io.github.springtestify.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation for validating performance requirements in tests.
 * <p>
 * This annotation allows specifying performance thresholds for test methods.
 * <p>
 * Example usage:
 * <pre>
 * &#064;PerformanceTest(threshold = "50ms")
 * public void testUserLookup() {
 *     // Test method
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceTest {
    /**
     * The performance threshold for the test.
     * <p>
     * This can be specified in milliseconds (e.g., "50ms") or other time units.
     * @return the performance threshold
     */
    String threshold();

    /**
     * The number of warmup iterations to run before measuring performance.
     * @return the number of warmup iterations
     */
    int warmupIterations() default 2;

    /**
     * The number of measurement iterations to run.
     * @return the number of measurement iterations
     */
    int measurementIterations() default 5;

    /**
     * Whether to fail the test if the performance threshold is exceeded.
     * @return true if the test should fail when the threshold is exceeded, false otherwise
     */
    boolean failOnViolation() default true;
}

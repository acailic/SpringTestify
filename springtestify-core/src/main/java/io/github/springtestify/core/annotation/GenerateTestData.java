package io.github.springtestify.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation for automatically generating test data based on entity classes.
 * <p>
 * This annotation allows specifying which entities to generate data for, along with
 * configuration options for the generated data.
 * <p>
 * Example usage:
 * <pre>
 * &#064;GenerateTestData(entity = User.class, count = 10, properties = {"role=ADMIN:2,USER:8"})
 * public class UserServiceTest {
 *     // Test methods
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GenerateTestData {
    /**
     * The entity class to generate data for.
     * @return the entity class
     */
    Class<?> entity();

    /**
     * The number of entities to generate.
     * @return the number of entities
     */
    int count() default 1;

    /**
     * Property configurations for the generated entities.
     * <p>
     * Each property configuration is in the format "propertyName=value1:count1,value2:count2".
     * For example, "role=ADMIN:2,USER:8" will generate 2 entities with role=ADMIN and 8 with role=USER.
     * @return the property configurations
     */
    String[] properties() default {};

    /**
     * Whether to persist the generated entities to the database.
     * @return true if the entities should be persisted, false otherwise
     */
    boolean persist() default true;

    /**
     * The seed to use for random data generation.
     * <p>
     * Using the same seed will result in the same generated data across test runs.
     * @return the random seed
     */
    long seed() default 0L;
}

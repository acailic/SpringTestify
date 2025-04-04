package io.github.springtestify.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation for loading test data from files.
 * <p>
 * This annotation allows specifying one or more data files to load into the database
 * before running tests.
 * <p>
 * Example usage:
 * <pre>
 * &#064;DataSetup(value = {"users.json", "orders.json"})
 * public class UserServiceTest {
 *     // Test methods
 * }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSetup {
    /**
     * The data files to load.
     * <p>
     * These can be JSON, XML, CSV, or SQL files, depending on the configured data loaders.
     * @return the data files to load
     */
    String[] value() default {};

    /**
     * Whether to clear existing data before loading new data.
     * @return true if existing data should be cleared, false otherwise
     */
    boolean clearBeforeLoad() default true;

    /**
     * The format of the data files.
     * <p>
     * If not specified, the format will be inferred from the file extension.
     * @return the format of the data files
     */
    String format() default "";

    /**
     * The data type of the files.
     * <p>
     * Used for specific data loaders like MongoDB.
     * @return the data type
     */
    String dataType() default "";

    /**
     * The collection name for MongoDB data.
     * <p>
     * Only used with MongoDB database type.
     * @return the collection name
     */
    String collection() default "";
}

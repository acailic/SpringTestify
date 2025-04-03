package io.github.springtestify.core.annotation;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for testing Spring Data JPA repositories.
 * <p>
 * This annotation is a meta-annotation that combines Spring's {@link DataJpaTest}
 * with additional configuration to simplify repository testing.
 * <p>
 * Example usage:
 * <pre>
 * &#064;RepositoryTest(populateWith = "users.sql")
 * public class UserRepositoryTest {
 *     // Test methods
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DataJpaTest
public @interface RepositoryTest {
    /**
     * Alias for {@link DataJpaTest#properties}.
     * <p>
     * Specifies the properties to apply to the test context.
     * @return the properties to apply
     */
    @AliasFor(annotation = DataJpaTest.class, attribute = "properties")
    String[] properties() default {};

    /**
     * The SQL script to use for populating the database.
     * <p>
     * This can be used to automatically populate the database with test data.
     * @return the SQL script to use
     */
    String populateWith() default "";

    /**
     * Whether to use transactions for test methods.
     * <p>
     * By default, each test method runs within a transaction that is rolled back
     * after the method completes.
     * @return true if transactions should be used, false otherwise
     */
    boolean useTransactions() default true;
}

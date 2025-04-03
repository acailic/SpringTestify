package io.github.springtestify.core.annotation;

import io.github.springtestify.core.enums.DbType;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotation for configuring an in-memory database for testing.
 * <p>
 * This annotation allows specifying which in-memory database to use for testing,
 * along with configuration options.
 * <p>
 * Example usage:
 * <pre>
 * &#064;InMemoryDb(type = DbType.POSTGRES_COMPATIBLE, migrate = true)
 * public class UserRepositoryTest {
 *     // Test methods
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigureTestDatabase
public @interface InMemoryDb {
    /**
     * The type of in-memory database to use.
     * @return the database type
     */
    DbType type() default DbType.H2;

    /**
     * Whether to apply database migrations.
     * @return true if migrations should be applied, false otherwise
     */
    boolean migrate() default false;

    /**
     * The location of SQL scripts to execute to initialize the database.
     * @return the locations of SQL scripts
     */
    String[] scripts() default {};

    /**
     * Alias for {@link AutoConfigureTestDatabase#replace}.
     * <p>
     * Determines what type of existing DataSource beans can be replaced.
     * @return the replacement type
     */
    @AliasFor(annotation = AutoConfigureTestDatabase.class, attribute = "replace")
    AutoConfigureTestDatabase.Replace replace() default AutoConfigureTestDatabase.Replace.ANY;
}

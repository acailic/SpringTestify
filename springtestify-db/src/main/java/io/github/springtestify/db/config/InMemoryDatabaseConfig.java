package io.github.springtestify.db.config;

import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.enums.DbType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for setting up in-memory databases for testing.
 * <p>
 * This class configures a DataSource based on the {@link InMemoryDb} annotation
 * settings found on the test class.
 */
@TestConfiguration
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnClass(DataSource.class)
public class InMemoryDatabaseConfig {

    private final Environment environment;

    private static final Map<DbType, String> DB_URLS = new HashMap<>();
    private static final Map<DbType, String> DB_DRIVERS = new HashMap<>();

    static {
        // H2 configurations
        DB_URLS.put(DbType.H2, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        DB_DRIVERS.put(DbType.H2, "org.h2.Driver");

        // H2 with MySQL compatibility
        DB_URLS.put(DbType.MYSQL_COMPATIBLE, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL");
        DB_DRIVERS.put(DbType.MYSQL_COMPATIBLE, "org.h2.Driver");

        // H2 with PostgreSQL compatibility
        DB_URLS.put(DbType.POSTGRES_COMPATIBLE, "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL");
        DB_DRIVERS.put(DbType.POSTGRES_COMPATIBLE, "org.h2.Driver");

        // HSQLDB configuration
        DB_URLS.put(DbType.HSQLDB, "jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1");
        DB_DRIVERS.put(DbType.HSQLDB, "org.hsqldb.jdbc.JDBCDriver");

        // Derby configuration
        DB_URLS.put(DbType.DERBY, "jdbc:derby:memory:testdb;create=true");
        DB_DRIVERS.put(DbType.DERBY, "org.apache.derby.jdbc.EmbeddedDriver");
    }

    public InMemoryDatabaseConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * Creates a DataSource based on the {@link InMemoryDb} annotation settings.
     * <p>
     * If no {@link InMemoryDb} annotation is found, defaults to H2.
     *
     * @param properties the DataSource properties
     * @return the configured DataSource
     */
    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        // Default to H2 if no specific configuration is found
        DbType dbType = DbType.H2;

        // Check for custom database type configuration
        String dbTypeProperty = environment.getProperty("spring.test.database.type");
        if (dbTypeProperty != null && !dbTypeProperty.isEmpty()) {
            try {
                dbType = DbType.valueOf(dbTypeProperty.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Fallback to default if the specified type is invalid
            }
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DB_DRIVERS.getOrDefault(dbType, DB_DRIVERS.get(DbType.H2)));
        dataSource.setUrl(DB_URLS.getOrDefault(dbType, DB_URLS.get(DbType.H2)));
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());

        return dataSource;
    }
}

package io.github.springtestify.db.initializer;

import io.github.springtestify.core.annotation.InMemoryDb;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Database initializer that executes SQL scripts specified in the {@link InMemoryDb} annotation.
 * <p>
 * This class listens for the {@link ContextRefreshedEvent} and executes any SQL scripts
 * specified in the {@link InMemoryDb} annotation on the test class.
 */
@TestConfiguration
public class DatabaseInitializer extends AbstractTestExecutionListener
        implements ApplicationListener<ContextRefreshedEvent> {

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;

    public DatabaseInitializer(ResourceLoader resourceLoader, DataSource dataSource) {
        this.resourceLoader = resourceLoader;
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // This method will be called when the application context is refreshed
        // We don't need to do anything here as the initialization happens in beforeTestClass
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        Class<?> testClass = testContext.getTestClass();
        InMemoryDb annotation = testClass.getAnnotation(InMemoryDb.class);

        if (annotation != null && annotation.scripts().length > 0) {
            executeScripts(annotation.scripts());
        }
    }

    /**
     * Executes the specified SQL scripts against the configured DataSource.
     *
     * @param scriptLocations the locations of the SQL scripts to execute
     */
    private void executeScripts(String[] scriptLocations) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();

        Arrays.stream(scriptLocations)
                .map(resourceLoader::getResource)
                .forEach(populator::addScript);

        populator.execute(dataSource);
    }

    /**
     * Checks if the specified script exists as a resource.
     *
     * @param scriptLocation the location of the script
     * @return true if the script exists, false otherwise
     */
    private boolean scriptExists(String scriptLocation) {
        Resource resource = resourceLoader.getResource(scriptLocation);
        return resource.exists();
    }
}

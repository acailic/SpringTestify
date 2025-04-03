package io.github.springtestify.db.loader;

import io.github.springtestify.core.annotation.DataSetup;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;

/**
 * Data loader that loads test data from files specified in the {@link DataSetup} annotation.
 * <p>
 * This class is responsible for loading test data from files in various formats (JSON, XML, CSV)
 * and persisting it to the database.
 */
public class DataLoader extends AbstractTestExecutionListener {

    @PersistenceContext
    private EntityManager entityManager;

    private final ResourceLoader resourceLoader;

    public DataLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        Class<?> testClass = testContext.getTestClass();
        DataSetup annotation = testClass.getAnnotation(DataSetup.class);

        if (annotation != null) {
            loadData(annotation, testContext);
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        DataSetup annotation = testContext.getTestMethod().getAnnotation(DataSetup.class);

        if (annotation != null) {
            loadData(annotation, testContext);
        }
    }

    /**
     * Loads data from the files specified in the {@link DataSetup} annotation.
     *
     * @param annotation the DataSetup annotation
     * @param testContext the test context
     */
    private void loadData(DataSetup annotation, TestContext testContext) {
        if (annotation.clearBeforeLoad()) {
            clearData();
        }

        Arrays.stream(annotation.value())
                .forEach(file -> loadFile(file, annotation.format(), testContext));
    }

    /**
     * Clears all data from the database.
     */
    private void clearData() {
        // Implementation would depend on the specific requirements
        // This could involve truncating tables or executing delete statements
    }

    /**
     * Loads data from a file into the database.
     *
     * @param file the file to load
     * @param format the format of the file (JSON, XML, CSV)
     * @param testContext the test context
     */
    private void loadFile(String file, String format, TestContext testContext) {
        Resource resource = resourceLoader.getResource(file);

        if (!resource.exists()) {
            throw new IllegalArgumentException("Data file not found: " + file);
        }

        String fileFormat = format.isEmpty() ? determineFormat(file) : format;

        // Load the data based on the file format
        // This would involve parsing the file and persisting the data to the database
        // The implementation would depend on the specific requirements and supported formats
    }

    /**
     * Determines the format of a file based on its extension.
     *
     * @param file the file
     * @return the format of the file
     */
    private String determineFormat(String file) {
        if (file.endsWith(".json")) {
            return "json";
        } else if (file.endsWith(".xml")) {
            return "xml";
        } else if (file.endsWith(".csv")) {
            return "csv";
        } else if (file.endsWith(".sql")) {
            return "sql";
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + file);
        }
    }
}

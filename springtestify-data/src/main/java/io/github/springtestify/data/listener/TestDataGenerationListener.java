package io.github.springtestify.data.listener;

import io.github.springtestify.core.annotation.GenerateTestData;
import io.github.springtestify.data.generator.TestDataGenerator;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test execution listener that processes {@link GenerateTestData} annotations
 * and generates test data accordingly.
 * <p>
 * This listener:
 * <ul>
 *   <li>Detects {@code @GenerateTestData} annotations on the test class</li>
 *   <li>Generates entity instances based on the annotation parameters</li>
 *   <li>Saves the generated entities to the database if a repository is available</li>
 *   <li>Makes the generated entities available to the test class through a registry</li>
 * </ul>
 */
public class TestDataGenerationListener extends AbstractTestExecutionListener {

    private static final String GENERATED_DATA_ATTRIBUTE = "io.github.springtestify.generatedData";

    @Override
    public void beforeTestClass(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        
        // Get @GenerateTestData annotations from the class
        Set<GenerateTestData> annotations = collectTestDataAnnotations(testClass);
        
        // Skip if no annotations
        if (annotations.isEmpty()) {
            return;
        }
        
        // Get the test data generator from the application context
        TestDataGenerator dataGenerator = testContext.getApplicationContext().getBean(TestDataGenerator.class);
        
        // Generate and store data for each annotation
        Map<Class<?>, List<?>> generatedData = new HashMap<>();
        
        for (GenerateTestData annotation : annotations) {
            Class<?> entityClass = annotation.entity();
            int count = annotation.count();
            Map<String, String> propertyValues = extractPropertyValues(annotation);
            
            // Generate and save entities
            List<?> entities = dataGenerator.generate(entityClass, count, propertyValues);
            entities = dataGenerator.saveAll(entities);
            
            // Store generated entities by entity class
            generatedData.put(entityClass, entities);
        }
        
        // Store the generated data in the test context
        testContext.setAttribute(GENERATED_DATA_ATTRIBUTE, generatedData);
    }
    
    @Override
    public void prepareTestInstance(TestContext testContext) {
        // Make generated data available to the test instance
        Map<Class<?>, List<?>> generatedData = 
                (Map<Class<?>, List<?>>) testContext.getAttribute(GENERATED_DATA_ATTRIBUTE);
        
        if (generatedData != null && !generatedData.isEmpty()) {
            // Register a bean in the application context to access the generated data
            testContext.getApplicationContext()
                    .getAutowireCapableBeanFactory()
                    .registerSingleton("springTestifyGeneratedData", generatedData);
        }
    }
    
    /**
     * Collects all {@link GenerateTestData} annotations on the test class.
     * <p>
     * This includes annotations directly on the class and repeatable annotations.
     *
     * @param testClass the test class
     * @return the set of annotations
     */
    private Set<GenerateTestData> collectTestDataAnnotations(Class<?> testClass) {
        return AnnotatedElementUtils.findMergedRepeatableAnnotations(testClass, GenerateTestData.class);
    }
    
    /**
     * Extracts property values from the annotation.
     *
     * @param annotation the annotation
     * @return a map of property names to value specifications
     */
    private Map<String, String> extractPropertyValues(GenerateTestData annotation) {
        Map<String, String> propertyValues = new HashMap<>();
        
        String[] properties = annotation.properties();
        for (String property : properties) {
            int equalsIndex = property.indexOf('=');
            if (equalsIndex > 0 && equalsIndex < property.length() - 1) {
                String name = property.substring(0, equalsIndex).trim();
                String value = property.substring(equalsIndex + 1).trim();
                propertyValues.put(name, value);
            }
        }
        
        return propertyValues;
    }
    
    @Override
    public int getOrder() {
        return 2500; // Run after the standard Spring test listeners and other SpringTestify listeners
    }
}
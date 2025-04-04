package io.github.springtestify.config;

import io.github.springtestify.core.annotation.CrudControllerTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

public class TestConfigurationProcessor extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        processCrudControllerTest(testClass);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        Method testMethod = testContext.getTestMethod();
        processTestMethod(testMethod);
    }

    private void processCrudControllerTest(Class<?> testClass) {
        CrudControllerTest annotation = AnnotatedElementUtils.findMergedAnnotation(testClass, CrudControllerTest.class);
        if (annotation != null) {
            configureTestClass(testClass, annotation);
        }
    }

    private void configureTestClass(Class<?> testClass, CrudControllerTest annotation) {
        // Set system properties based on annotation values
        setSystemProperty("spring.testify.test.enable-method-ordering",
                         String.valueOf(annotation.enableMethodOrdering()));
        setSystemProperty("spring.testify.test.enable-auto-mock-mvc",
                         String.valueOf(annotation.enableAutoMockMvc()));
        setSystemProperty("spring.testify.test.enable-auto-object-mapper",
                         String.valueOf(annotation.enableAutoObjectMapper()));
        setSystemProperty("spring.testify.enable-smart-context-caching",
                         String.valueOf(annotation.enableSmartContextCaching()));

        // Set custom properties from annotation
        Arrays.stream(annotation.properties())
              .forEach(property -> {
                  String[] parts = property.split("=", 2);
                  if (parts.length == 2) {
                      setSystemProperty(parts[0], parts[1]);
                  }
              });
    }

    private void processTestMethod(Method testMethod) {
        // Process method-level configurations if needed
        ReflectionUtils.makeAccessible(testMethod);
        // Add method-specific configurations here
    }

    private void setSystemProperty(String key, String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE; // Execute this listener first
    }
}

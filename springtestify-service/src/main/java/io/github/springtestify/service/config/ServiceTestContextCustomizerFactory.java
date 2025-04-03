package io.github.springtestify.service.config;

import io.github.springtestify.core.annotation.ServiceTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Factory for creating {@link ServiceTestContextCustomizer} instances.
 * <p>
 * This factory creates customizers for test classes annotated with {@link ServiceTest}.
 */
public class ServiceTestContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass,
                                                    List<ContextConfigurationAttributes> configAttributes) {
        // Check if the test class is annotated with @ServiceTest
        ServiceTest annotation = AnnotatedElementUtils.findMergedAnnotation(testClass, ServiceTest.class);

        if (annotation != null) {
            // Extract settings from the annotation
            Class<?> serviceClass = annotation.service();
            boolean autoMock = annotation.mockDependencies();
            Class<?>[] mockConfigClasses = annotation.classes();

            // Create and return a customizer with the settings
            return new ServiceTestContextCustomizer(serviceClass, autoMock, Arrays.asList(mockConfigClasses));
        }

        return null;
    }
}

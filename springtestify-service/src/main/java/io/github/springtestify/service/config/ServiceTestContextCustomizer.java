package io.github.springtestify.service.config;

import io.github.springtestify.core.annotation.ServiceTest;
import io.github.springtestify.service.mock.MockFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

import java.util.List;

/**
 * A context customizer for service tests.
 * <p>
 * This customizer:
 * <ul>
 *   <li>Registers a MockFactory bean</li>
 *   <li>Configures mocks for service dependencies</li>
 *   <li>Sets up the service under test with mocked dependencies</li>
 * </ul>
 */
public class ServiceTestContextCustomizer implements ContextCustomizer {

    private final Class<?> serviceClass;
    private final boolean autoMock;
    private final List<Class<?>> mockConfigurations;

    /**
     * Creates a new ServiceTestContextCustomizer with the specified service class and settings.
     *
     * @param serviceClass the service class under test
     * @param autoMock whether to automatically mock all dependencies
     * @param mockConfigurations the mock configuration classes
     */
    public ServiceTestContextCustomizer(Class<?> serviceClass, boolean autoMock, List<Class<?>> mockConfigurations) {
        this.serviceClass = serviceClass;
        this.autoMock = autoMock;
        this.mockConfigurations = mockConfigurations;
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        
        // Register MockFactory as a bean
        MockFactory mockFactory = new MockFactory();
        beanFactory.registerSingleton("mockFactory", mockFactory);
        
        // If auto-mocking is enabled, set up mocks for service dependencies
        if (autoMock && serviceClass != Void.class) {
            mockFactory.registerMocksForService(beanFactory, serviceClass);
        }
        
        // If there are any mock configuration classes, instantiate them
        for (Class<?> configClass : mockConfigurations) {
            try {
                Object configInstance = configClass.getDeclaredConstructor().newInstance();
                String beanName = configClass.getSimpleName() + "Instance";
                beanFactory.registerSingleton(beanName, configInstance);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create instance of mock configuration class " + configClass.getName(), e);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        
        ServiceTestContextCustomizer other = (ServiceTestContextCustomizer) obj;
        return this.serviceClass.equals(other.serviceClass) &&
               this.autoMock == other.autoMock &&
               this.mockConfigurations.equals(other.mockConfigurations);
    }

    @Override
    public int hashCode() {
        int result = serviceClass.hashCode();
        result = 31 * result + Boolean.hashCode(autoMock);
        result = 31 * result + mockConfigurations.hashCode();
        return result;
    }
}
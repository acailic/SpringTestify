package io.github.springtestify.service.mock;

import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for creating and managing mocks for service dependencies.
 * <p>
 * This class provides methods to create mocks for dependencies of a service class
 * and register them in the Spring application context.
 */
public class MockFactory {

    private final Map<Class<?>, Object> mocks = new HashMap<>();
    
    /**
     * Creates a mock for the specified class if one doesn't already exist.
     *
     * @param <T> the type to mock
     * @param clazz the class to mock
     * @return the mock instance
     */
    @SuppressWarnings("unchecked")
    public <T> T createMock(Class<T> clazz) {
        return (T) mocks.computeIfAbsent(clazz, Mockito::mock);
    }
    
    /**
     * Registers mocks for all dependencies of the specified service class.
     *
     * @param beanFactory the bean factory to register mocks with
     * @param serviceClass the service class to analyze for dependencies
     * @return an instance of the service with mocked dependencies
     */
    public Object registerMocksForService(ConfigurableListableBeanFactory beanFactory, Class<?> serviceClass) {
        try {
            // Create an instance of the service
            Object service = serviceClass.getDeclaredConstructor().newInstance();
            
            // Find all fields that could be dependencies
            ReflectionUtils.doWithFields(serviceClass, field -> {
                // Process fields that are annotated with @Autowired, @Inject, etc.
                if (isInjectableField(field)) {
                    processDependencyField(field, service, beanFactory);
                }
            });
            
            // Register the service in the context
            String serviceBeanName = serviceClass.getSimpleName().substring(0, 1).toLowerCase() + 
                                    serviceClass.getSimpleName().substring(1);
            beanFactory.registerSingleton(serviceBeanName, service);
            
            return service;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create service instance for " + serviceClass.getName(), e);
        }
    }
    
    /**
     * Processes a dependency field by creating and injecting a mock.
     *
     * @param field the field representing a dependency
     * @param service the service instance
     * @param beanFactory the bean factory to register mocks with
     */
    private void processDependencyField(Field field, Object service, ConfigurableListableBeanFactory beanFactory) {
        field.setAccessible(true);
        
        Class<?> dependencyType = field.getType();
        Object mock = createMock(dependencyType);
        
        try {
            // Inject the mock into the service
            field.set(service, mock);
            
            // Register the mock in the context
            String mockBeanName = field.getName();
            beanFactory.registerSingleton(mockBeanName, mock);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to inject mock for " + field.getName(), e);
        }
    }
    
    /**
     * Checks if a field is injectable (has an autowiring annotation).
     *
     * @param field the field to check
     * @return true if the field is injectable, false otherwise
     */
    private boolean isInjectableField(Field field) {
        // Check for common injection annotations like @Autowired, @Inject, etc.
        return field.isAnnotationPresent(org.springframework.beans.factory.annotation.Autowired.class) ||
               field.isAnnotationPresent(javax.inject.Inject.class) ||
               // Add additional annotations if needed
               false;
    }
    
    /**
     * Gets a previously created mock for the specified class.
     *
     * @param <T> the type of the mock
     * @param clazz the class of the mock
     * @return the mock instance, or null if no mock exists for the class
     */
    @SuppressWarnings("unchecked")
    public <T> T getMock(Class<T> clazz) {
        return (T) mocks.get(clazz);
    }
    
    /**
     * Resets all mocks.
     */
    public void reset() {
        mocks.values().forEach(Mockito::reset);
    }
    
    /**
     * Clears all mocks.
     */
    public void clear() {
        mocks.clear();
    }
}
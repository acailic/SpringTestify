package io.github.springtestify.service.listener;

import io.github.springtestify.core.annotation.ServiceTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Test execution listener that processes the {@link ServiceTest} annotation and configures
 * the test environment for service testing.
 * <p>
 * This listener:
 * <ul>
 *   <li>Configures the test context to include only the service layer</li>
 *   <li>Sets up mocks for all dependencies of the service</li>
 *   <li>Registers any specified stubbing configurations</li>
 * </ul>
 */
public class ServiceTestExecutionListener extends AbstractTestExecutionListener {

    private static final String SERVICE_UNDER_TEST_ATTRIBUTE = "io.github.springtestify.serviceUnderTest";
    private static final String AUTO_MOCK_ATTRIBUTE = "io.github.springtestify.autoMockEnabled";

    @Override
    public void beforeTestClass(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        ServiceTest annotation = AnnotatedElementUtils.findMergedAnnotation(testClass, ServiceTest.class);
        
        if (annotation != null) {
            // Store service class for later use
            Class<?> serviceClass = annotation.value();
            if (serviceClass != Void.class) {
                testContext.setAttribute(SERVICE_UNDER_TEST_ATTRIBUTE, serviceClass);
            }
            
            // Store auto-mock setting
            boolean autoMock = annotation.autoMock();
            testContext.setAttribute(AUTO_MOCK_ATTRIBUTE, autoMock);
        }
    }
    
    @Override
    public void prepareTestInstance(TestContext testContext) {
        Class<?> serviceClass = (Class<?>) testContext.getAttribute(SERVICE_UNDER_TEST_ATTRIBUTE);
        Boolean autoMock = (Boolean) testContext.getAttribute(AUTO_MOCK_ATTRIBUTE);
        
        if (serviceClass != null && autoMock != null && autoMock) {
            // If auto-mocking is enabled, configure all dependencies of the service to be mocked
            registerServiceAndMockedDependencies(testContext, serviceClass);
        }
    }
    
    /**
     * Registers the service under test and mocked dependencies.
     *
     * @param testContext the test context
     * @param serviceClass the service class
     */
    private void registerServiceAndMockedDependencies(TestContext testContext, Class<?> serviceClass) {
        // This would be implemented to analyze the service class and register mocks for its dependencies
        // For now, we'll keep this as a placeholder for the implementation
    }
    
    @Override
    public int getOrder() {
        return 2000; // Run after the standard Spring test listeners
    }
}
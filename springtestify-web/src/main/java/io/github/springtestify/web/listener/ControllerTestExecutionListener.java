package io.github.springtestify.web.listener;

import io.github.springtestify.core.annotation.ControllerTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test execution listener that configures the test environment based on the {@link ControllerTest} annotation.
 * <p>
 * This listener:
 * <ul>
 *   <li>Sets up the {@link MockMvc} instance with the proper configuration based on annotation settings</li>
 *   <li>Registers request path information for use in tests</li>
 *   <li>Configures security if specified in the annotation</li>
 * </ul>
 */
public class ControllerTestExecutionListener extends AbstractTestExecutionListener {

    private static final String BASE_PATH_ATTRIBUTE = "io.github.springtestify.basePath";
    private static final String MOCK_MVC_ATTRIBUTE = "io.github.springtestify.mockMvc";

    @Override
    public void beforeTestClass(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        ControllerTest annotation = AnnotatedElementUtils.findMergedAnnotation(testClass, ControllerTest.class);
        
        if (annotation != null) {
            String basePath = annotation.path().trim();
            
            // Store the base path for later use
            testContext.setAttribute(BASE_PATH_ATTRIBUTE, basePath);
            
            // Configure MockMvc if not already done
            WebApplicationContext webContext = testContext.getApplicationContext();
            if (webContext != null && !testContext.hasAttribute(MOCK_MVC_ATTRIBUTE)) {
                MockMvc mockMvc;
                
                if (annotation.withSecurity()) {
                    mockMvc = MockMvcBuilders.webAppContextSetup(webContext)
                            .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                            .build();
                } else {
                    mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
                }
                
                testContext.setAttribute(MOCK_MVC_ATTRIBUTE, mockMvc);
            }
        }
    }
    
    @Override
    public void beforeTestMethod(TestContext testContext) {
        // Ensure attributes are available to the test method
        String basePath = (String) testContext.getAttribute(BASE_PATH_ATTRIBUTE);
        MockMvc mockMvc = (MockMvc) testContext.getAttribute(MOCK_MVC_ATTRIBUTE);
        
        if (basePath != null) {
            testContext.getApplicationContext()
                    .getAutowireCapableBeanFactory()
                    .registerSingleton("springTestifyBasePath", basePath);
        }
        
        if (mockMvc != null) {
            testContext.getApplicationContext()
                    .getAutowireCapableBeanFactory()
                    .registerSingleton("springTestifyMockMvc", mockMvc);
        }
    }
    
    @Override
    public int getOrder() {
        return 2000; // Run after the standard Spring test listeners
    }
}
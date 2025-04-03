package io.github.springtestify.web.config;

import io.github.springtestify.web.listener.ControllerTestExecutionListener;
import io.github.springtestify.web.util.ApiRequestBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

/**
 * Configuration for web testing support in SpringTestify.
 * <p>
 * This class registers the necessary beans for controller testing support.
 */
@TestConfiguration
public class WebTestConfig {

    /**
     * Creates a ControllerTestExecutionListener bean.
     *
     * @return the ControllerTestExecutionListener
     */
    @Bean
    @ConditionalOnMissingBean
    public ControllerTestExecutionListener controllerTestExecutionListener() {
        return new ControllerTestExecutionListener();
    }
    
    /**
     * Creates an ApiRequestBuilder bean using the base path from the test context.
     * <p>
     * This bean is lazy-initialized as it depends on the base path being set by the
     * ControllerTestExecutionListener, which happens during test execution.
     *
     * @param basePath the base path for API requests, injected from the test context
     * @return the ApiRequestBuilder
     */
    @Bean
    @Lazy
    @ConditionalOnMissingBean
    public ApiRequestBuilder apiRequestBuilder(String springTestifyBasePath) {
        return new ApiRequestBuilder(springTestifyBasePath);
    }
}
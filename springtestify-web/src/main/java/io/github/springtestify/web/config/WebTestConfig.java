package io.github.springtestify.web.config;

import io.github.springtestify.web.util.ApiRequestBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for web test utilities.
 * <p>
 * This configuration provides:
 * <ul>
 * <li>API request builder configuration</li>
 * <li>Base path configuration for API requests</li>
 * </ul>
 */
@Configuration
public class WebTestConfig {

    /**
     * Creates an {@link ApiRequestBuilder} instance configured with the base path.
     *
     * @param springTestifyBasePath the base path for API requests, injected from
     *                              the test context
     * @return configured {@link ApiRequestBuilder} instance
     */
    @Bean
    public ApiRequestBuilder apiRequestBuilder(@Qualifier("springTestifyBasePath") String springTestifyBasePath) {
        return new ApiRequestBuilder(springTestifyBasePath);
    }
}

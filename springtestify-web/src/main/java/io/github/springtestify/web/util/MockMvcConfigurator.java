package io.github.springtestify.web.util;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utility class for configuring MockMvc instances with common settings.
 * <p>
 * Provides a fluent API for creating and configuring MockMvc instances for testing.
 */
public class MockMvcConfigurator {

    private final WebApplicationContext webApplicationContext;
    private final List<MockMvcConfigurer> configurers = new ArrayList<>();
    private final List<Consumer<ConfigurableMockMvcBuilder<?>>> customizers = new ArrayList<>();

    /**
     * Creates a new MockMvcConfigurator for the specified web application context.
     *
     * @param webApplicationContext the web application context
     */
    public MockMvcConfigurator(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    /**
     * Adds a MockMvcConfigurer to the configuration.
     *
     * @param configurer the configurer to add
     * @return this configurator, for method chaining
     */
    public MockMvcConfigurator with(MockMvcConfigurer configurer) {
        configurers.add(configurer);
        return this;
    }

    /**
     * Adds a custom configuration function to the configuration.
     *
     * @param customizer the customizer function to add
     * @return this configurator, for method chaining
     */
    public MockMvcConfigurator customize(Consumer<ConfigurableMockMvcBuilder<?>> customizer) {
        customizers.add(customizer);
        return this;
    }

    /**
     * Creates a new MockMvc instance with the configured settings.
     *
     * @return the configured MockMvc instance
     */
    public MockMvc build() {
        ConfigurableMockMvcBuilder<?> builder = MockMvcBuilders.webAppContextSetup(webApplicationContext);
        
        // Apply all configurers
        for (MockMvcConfigurer configurer : configurers) {
            builder.apply(configurer);
        }
        
        // Apply all customizers
        for (Consumer<ConfigurableMockMvcBuilder<?>> customizer : customizers) {
            customizer.accept(builder);
        }
        
        return builder.build();
    }

    /**
     * Helper method to create a GET request with JSON response type expectation.
     *
     * @param uri the URI to request
     * @return the request builder
     */
    public static MockHttpServletRequestBuilder get(String uri) {
        return MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * Helper method to create a POST request with JSON content type.
     *
     * @param uri the URI to request
     * @param content the JSON content to send
     * @return the request builder
     */
    public static MockHttpServletRequestBuilder post(String uri, String content) {
        return MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);
    }

    /**
     * Helper method to create a PUT request with JSON content type.
     *
     * @param uri the URI to request
     * @param content the JSON content to send
     * @return the request builder
     */
    public static MockHttpServletRequestBuilder put(String uri, String content) {
        return MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);
    }

    /**
     * Helper method to create a DELETE request.
     *
     * @param uri the URI to request
     * @return the request builder
     */
    public static MockHttpServletRequestBuilder delete(String uri) {
        return MockMvcRequestBuilders.delete(uri)
                .accept(MediaType.APPLICATION_JSON);
    }
}
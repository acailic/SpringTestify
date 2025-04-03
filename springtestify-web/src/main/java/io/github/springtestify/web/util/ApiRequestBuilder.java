package io.github.springtestify.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A builder for API requests in controller tests.
 * <p>
 * This builder helps create requests that include the base path from the {@code @ControllerTest} annotation.
 * It provides methods for common HTTP methods and handles path concatenation and query parameters.
 */
public class ApiRequestBuilder {

    private final String basePath;
    private final ObjectMapper objectMapper;

    /**
     * Creates a new ApiRequestBuilder with the specified base path.
     *
     * @param basePath the base path for API requests
     */
    public ApiRequestBuilder(String basePath) {
        this.basePath = basePath;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Creates a new ApiRequestBuilder with the specified base path and object mapper.
     *
     * @param basePath the base path for API requests
     * @param objectMapper the object mapper to use for serializing objects
     */
    public ApiRequestBuilder(String basePath, ObjectMapper objectMapper) {
        this.basePath = basePath;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a GET request builder for the specified path.
     *
     * @param path the path relative to the base path
     * @return the request builder
     */
    public MockHttpServletRequestBuilder get(String path) {
        return MockMvcRequestBuilders.get(buildFullPath(path))
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * Creates a GET request builder with query parameters.
     *
     * @param path the path relative to the base path
     * @param queryParams the query parameters
     * @return the request builder
     */
    public MockHttpServletRequestBuilder get(String path, MultiValueMap<String, String> queryParams) {
        String fullUrl = UriComponentsBuilder.fromPath(buildFullPath(path))
                .queryParams(queryParams)
                .build()
                .toUriString();
        
        return MockMvcRequestBuilders.get(fullUrl)
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * Creates a POST request builder for the specified path.
     *
     * @param path the path relative to the base path
     * @param body the request body object (will be serialized to JSON)
     * @return the request builder
     */
    public MockHttpServletRequestBuilder post(String path, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return MockMvcRequestBuilders.post(buildFullPath(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Creates a PUT request builder for the specified path.
     *
     * @param path the path relative to the base path
     * @param body the request body object (will be serialized to JSON)
     * @return the request builder
     */
    public MockHttpServletRequestBuilder put(String path, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return MockMvcRequestBuilders.put(buildFullPath(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Creates a DELETE request builder for the specified path.
     *
     * @param path the path relative to the base path
     * @return the request builder
     */
    public MockHttpServletRequestBuilder delete(String path) {
        return MockMvcRequestBuilders.delete(buildFullPath(path))
                .accept(MediaType.APPLICATION_JSON);
    }

    /**
     * Creates a PATCH request builder for the specified path.
     *
     * @param path the path relative to the base path
     * @param body the request body object (will be serialized to JSON)
     * @return the request builder
     */
    public MockHttpServletRequestBuilder patch(String path, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return MockMvcRequestBuilders.patch(buildFullPath(path))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Builds a full path by concatenating the base path and the relative path.
     *
     * @param path the relative path
     * @return the full path
     */
    private String buildFullPath(String path) {
        if (basePath == null || basePath.isEmpty()) {
            return path.startsWith("/") ? path : "/" + path;
        }
        
        String normalizedBasePath = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        
        return normalizedBasePath + normalizedPath;
    }
}
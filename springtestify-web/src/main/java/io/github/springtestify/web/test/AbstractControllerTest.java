package io.github.springtestify.web.test;

import io.github.springtestify.core.annotation.ControllerTest;
import io.github.springtestify.web.util.ApiAssertions;
import io.github.springtestify.web.util.ApiRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Base class for controller tests that provides common functionality and test
 * utilities.
 * <p>
 * This class provides:
 * <ul>
 * <li>Access to {@link MockMvc} for performing HTTP requests</li>
 * <li>Base path configuration for API requests</li>
 * <li>Request builder utility for constructing test requests</li>
 * </ul>
 * <p>
 * Classes extending this class should be annotated with {@link ControllerTest}.
 * <p>
 * Example usage:
 *
 * <pre>
 * &#064;ControllerTest(path = "/api/users")
 * public class UserControllerTest extends AbstractControllerTest {
 *
 *     &#064;Test
 *     void shouldReturnUsersList() throws Exception {
 *         performGet("/")
 *                 .andExpect(status().isOk())
 *                 .andExpect(jsonPath("$[0].name").exists());
 *
 *         // Or using the fluent assertion API:
 *         assertThat(performGet("/"))
 *                 .isSuccessful()
 *                 .hasJsonPath("$[0].name");
 *     }
 * }
 * </pre>
 */
public abstract class AbstractControllerTest {

    /**
     * The MockMvc instance for performing HTTP requests in tests.
     * This is automatically configured by the test context.
     */
    @Autowired
    protected MockMvc mockMvc;

    /**
     * The base path for API requests.
     * This is configured through the
     * {@link io.github.springtestify.core.annotation.ControllerTest} annotation.
     */
    @Autowired
    @Qualifier("springTestifyBasePath")
    protected String basePath;

    /**
     * The request builder utility for constructing test requests.
     * This is automatically configured based on the base path.
     */
    @Autowired
    protected ApiRequestBuilder requestBuilder;

    /**
     * Executes a GET request to the specified path.
     *
     * @param path the path relative to the base path
     * @return the result actions
     * @throws Exception if an error occurs
     */
    protected ResultActions performGet(String path) throws Exception {
        return mockMvc.perform(requestBuilder.get(path));
    }

    /**
     * Executes a POST request to the specified path with the specified body.
     *
     * @param path the path relative to the base path
     * @param body the request body object
     * @return the result actions
     * @throws Exception if an error occurs
     */
    protected ResultActions performPost(String path, Object body) throws Exception {
        return mockMvc.perform(requestBuilder.post(path, body));
    }

    /**
     * Executes a PUT request to the specified path with the specified body.
     *
     * @param path the path relative to the base path
     * @param body the request body object
     * @return the result actions
     * @throws Exception if an error occurs
     */
    protected ResultActions performPut(String path, Object body) throws Exception {
        return mockMvc.perform(requestBuilder.put(path, body));
    }

    /**
     * Executes a DELETE request to the specified path.
     *
     * @param path the path relative to the base path
     * @return the result actions
     * @throws Exception if an error occurs
     */
    protected ResultActions performDelete(String path) throws Exception {
        return mockMvc.perform(requestBuilder.delete(path));
    }

    /**
     * Executes a custom request.
     *
     * @param requestBuilder the request builder
     * @return the result actions
     * @throws Exception if an error occurs
     */
    protected ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(requestBuilder);
    }

    /**
     * Creates assertions for the specified result actions.
     *
     * @param resultActions the result actions
     * @return the assertions
     */
    protected ApiAssertions assertThat(ResultActions resultActions) {
        return ApiAssertions.assertThat(resultActions);
    }
}

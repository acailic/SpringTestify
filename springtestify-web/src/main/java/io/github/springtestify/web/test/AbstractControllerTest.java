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
 * Base class for controller tests using SpringTestify.
 * <p>
 * Provides convenient methods for executing requests and making assertions on responses.
 * Classes extending this class should be annotated with {@link ControllerTest}.
 * <p>
 * Example usage:
 * <pre>
 * &#064;ControllerTest(path = "/api/users")
 * public class UserControllerTest extends AbstractControllerTest {
 *
 *     &#064;Test
 *     void shouldReturnUsersList() throws Exception {
 *         performGet("/")
 *             .andExpect(status().isOk())
 *             .andExpect(jsonPath("$[0].name").exists());
 *
 *         // Or using the fluent assertion API:
 *         assertThat(performGet("/"))
 *             .isSuccessful()
 *             .hasJsonPath("$[0].name");
 *     }
 * }
 * </pre>
 */
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    @Qualifier("springTestifyBasePath")
    protected String basePath;

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
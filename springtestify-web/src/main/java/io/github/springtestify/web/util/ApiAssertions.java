package io.github.springtestify.web.util;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Utility class for making assertions on API responses.
 * <p>
 * Provides convenient methods for common assertions on API responses in a fluent API style.
 */
public class ApiAssertions {

    private final ResultActions resultActions;

    /**
     * Creates a new ApiAssertions for the specified result actions.
     *
     * @param resultActions the result actions to make assertions on
     */
    public ApiAssertions(ResultActions resultActions) {
        this.resultActions = resultActions;
    }

    /**
     * Asserts that the response has the specified status code.
     *
     * @param statusCode the expected status code
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions hasStatus(int statusCode) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().is(statusCode));
        return this;
    }

    /**
     * Asserts that the response has a successful status code (2xx).
     *
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions isSuccessful() throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        return this;
    }

    /**
     * Asserts that the response has a client error status code (4xx).
     *
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions isClientError() throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().is4xxClientError());
        return this;
    }

    /**
     * Asserts that the response has a server error status code (5xx).
     *
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions isServerError() throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.status().is5xxServerError());
        return this;
    }

    /**
     * Asserts that the response contains JSON matching the specified JSON path expression.
     *
     * @param jsonPath the JSON path expression
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions hasJsonPath(String jsonPath) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(jsonPath).exists());
        return this;
    }

    /**
     * Asserts that the response contains JSON matching the specified JSON path expression and value.
     *
     * @param jsonPath the JSON path expression
     * @param value the expected value
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions hasJsonPathWithValue(String jsonPath, Object value) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(jsonPath, Matchers.is(value)));
        return this;
    }

    /**
     * Asserts that the response contains JSON matching the specified JSON path expression and matcher.
     *
     * @param jsonPath the JSON path expression
     * @param matcher the matcher
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions hasJsonPathWithValue(String jsonPath, Matcher<?> matcher) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(jsonPath, matcher));
        return this;
    }

    /**
     * Asserts that the response has a header with the specified name.
     *
     * @param headerName the name of the header
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions hasHeader(String headerName) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.header().exists(headerName));
        return this;
    }

    /**
     * Asserts that the response has a header with the specified name and value.
     *
     * @param headerName the name of the header
     * @param headerValue the expected value of the header
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions hasHeaderWithValue(String headerName, String headerValue) throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.header().string(headerName, headerValue));
        return this;
    }

    /**
     * Asserts that the response content type is JSON.
     *
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions isJson() throws Exception {
        resultActions.andExpect(MockMvcResultMatchers.content().contentType("application/json"));
        return this;
    }

    /**
     * Applies a custom result matcher to the response.
     *
     * @param matcher the result matcher to apply
     * @return this instance, for method chaining
     * @throws Exception if the assertion fails
     */
    public ApiAssertions matches(ResultMatcher matcher) throws Exception {
        resultActions.andExpect(matcher);
        return this;
    }

    /**
     * Returns the underlying ResultActions for further assertions.
     *
     * @return the underlying ResultActions
     */
    public ResultActions andReturn() {
        return resultActions;
    }

    /**
     * Creates a new ApiAssertions instance for the specified ResultActions.
     *
     * @param resultActions the ResultActions to make assertions on
     * @return a new ApiAssertions instance
     */
    public static ApiAssertions assertThat(ResultActions resultActions) {
        return new ApiAssertions(resultActions);
    }
}
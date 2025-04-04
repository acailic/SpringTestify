package io.github.springtestify.test;

import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.function.Consumer;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Utility class for common test assertions
 */
public class TestAssertions {

    /**
     * Assert common page response structure and content
     */
    public static ResultMatcher[] assertPageResponse(int totalElements) {
        return new ResultMatcher[]{
            jsonPath("$.content").isArray(),
            jsonPath("$.totalElements").value(totalElements),
            jsonPath("$.totalPages").exists(),
            jsonPath("$.size").exists(),
            jsonPath("$.number").exists()
        };
    }

    /**
     * Assert common CRUD response fields
     */
    public static ResultMatcher[] assertCommonEntityFields(String idPath, Object expectedId) {
        return new ResultMatcher[]{
            jsonPath(idPath).value(expectedId),
            jsonPath("$.createdAt").exists(),
            jsonPath("$.updatedAt").exists()
        };
    }

    /**
     * Assert validation error response
     */
    public static ResultMatcher[] assertValidationError(String field, String message) {
        return new ResultMatcher[]{
            jsonPath("$.status").value(400),
            jsonPath("$.errors").isArray(),
            jsonPath("$.errors[0].field").value(field),
            jsonPath("$.errors[0].message").value(message)
        };
    }

    /**
     * Assert not found error response
     */
    public static ResultMatcher[] assertNotFoundError(String message) {
        return new ResultMatcher[]{
            jsonPath("$.status").value(404),
            jsonPath("$.message").value(message)
        };
    }

    /**
     * Chain multiple assertions on a ResultActions
     */
    public static ResultActions assertAll(ResultActions resultActions, ResultMatcher... matchers) throws Exception {
        for (ResultMatcher matcher : matchers) {
            resultActions.andExpect(matcher);
        }
        return resultActions;
    }
}

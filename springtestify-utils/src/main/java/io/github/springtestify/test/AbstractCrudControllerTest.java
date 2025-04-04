package io.github.springtestify.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.springtestify.annotation.CrudControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for CRUD controller tests.
 * Provides common functionality for testing CRUD operations.
 *
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <S> Service type
 */
public abstract class AbstractCrudControllerTest<T, ID, S> extends AbstractScenarioTest<T> {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected S service;

    protected AbstractCrudControllerTest(Class<T> entityClass) {
        super(entityClass);
    }

    @Override
    protected String getBasePath() {
        CrudControllerTest annotation = getClass().getAnnotation(CrudControllerTest.class);
        if (annotation == null) {
            throw new IllegalStateException("Class must be annotated with @CrudControllerTest");
        }
        return annotation.path().isEmpty() ? annotation.value() : annotation.path();
    }

    /**
     * Get the test entity ID for use in path variables
     */
    protected abstract ID getTestEntityId();

    /**
     * Get common matchers to apply to all entity responses
     */
    protected abstract ResultMatcher[] getCommonEntityMatchers();

    /**
     * Get the path for a specific entity by ID
     */
    protected String getEntityPath() {
        return getBasePath() + "/{id}";
    }

    /**
     * Get the path for entity collection operations
     */
    protected String getCollectionPath() {
        return getBasePath();
    }

    /**
     * Get the path for a specific entity by its ID
     */
    protected String getEntityPath(ID id) {
        return getBasePath() + "/" + id;
    }

    protected T createTestEntity() {
        return buildFromCurrentScenario();
    }

    protected void assertFields(T entity) throws Exception {
        String json = objectMapper.writeValueAsString(entity);
        mockMvc.perform(post(getBasePath())
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated())
            .andExpectAll(getCommonEntityMatchers());
    }
}

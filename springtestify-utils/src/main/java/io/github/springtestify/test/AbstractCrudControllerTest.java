package io.github.springtestify.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base class for testing CRUD controllers
 * @param <T> Entity type
 * @param <ID> ID type
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

    protected T createTestEntity() {
        return buildFromCurrentScenario();
    }

    protected abstract ID getTestEntityId();

    protected abstract ResultMatcher[] getCommonEntityMatchers();

    protected void assertFields(T entity) throws Exception {
        String json = objectMapper.writeValueAsString(entity);
        mockMvc.perform(post(getBasePath())
                .contentType("application/json")
                .content(json))
            .andExpect(status().isCreated())
            .andExpectAll(getCommonEntityMatchers());
    }

    protected String getBasePath() {
        return "/api/" + entityClass.getSimpleName().toLowerCase() + "s";
    }
}

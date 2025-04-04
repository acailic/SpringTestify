package io.github.springtestify.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.springtestify.annotation.Expect;
import io.github.springtestify.annotation.ScenarioAction;
import io.github.springtestify.annotation.TestScenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base class for tests that use scenarios.
 * Provides utility methods for working with test scenarios.
 */
@ExtendWith(ScenarioTestExecutionListener.class)
public abstract class AbstractScenarioTest<T> {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected TestEntityBuilder<T> builder;
    protected final Class<T> entityClass;

    protected AbstractScenarioTest(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @BeforeEach
    void initBuilder() {
        this.builder = new TestEntityBuilder<>(entityClass);
    }

    /**
     * Execute the test scenario with the given method
     */
    protected void executeScenario(Method method) throws Exception {
        TestScenario testScenario = method.getAnnotation(TestScenario.class);
        ScenarioAction action = method.getAnnotation(ScenarioAction.class);

        if (testScenario == null || action == null) {
            throw new IllegalStateException("Both @TestScenario and @ScenarioAction annotations are required");
        }

        T entity = new TestEntityBuilder<>(getEntityClass())
                .withScenario(testScenario.value())
                .build();

        ResultActions result = performRequest(action, entity);
        verifyExpectations(testScenario.expect(), result);
    }

    protected ResultActions performRequest(ScenarioAction action, T entity) throws Exception {
        MockHttpServletRequestBuilder request = createRequest(action, entity);

        // Add request parameters if specified
        if (action.params().length > 0) {
            Arrays.stream(action.params()).forEach(param ->
                request.param(param.name(), param.value())
            );
        }

        // Add request headers if specified
        if (action.headers().length > 0) {
            Arrays.stream(action.headers()).forEach(header ->
                request.header(header.name(), header.value())
            );
        }

        return mockMvc.perform(request);
    }

    protected MockHttpServletRequestBuilder createRequest(ScenarioAction action, T entity) throws Exception {
        String path = action.path().isEmpty() ? getBasePath() : action.path();
        MockHttpServletRequestBuilder request;

        switch (action.method()) {
            case GET:
                request = MockMvcRequestBuilders.get(path);
                break;
            case POST:
                request = MockMvcRequestBuilders.post(path);
                break;
            case PUT:
                request = MockMvcRequestBuilders.put(path);
                break;
            case DELETE:
                request = MockMvcRequestBuilders.delete(path);
                break;
            case PATCH:
                request = MockMvcRequestBuilders.patch(path);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + action.method());
        }

        if (action.includeBody() && entity != null &&
            (action.method() == HttpMethod.POST ||
             action.method() == HttpMethod.PUT ||
             action.method() == HttpMethod.PATCH)) {
            request.contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(entity));
        }

        return request;
    }

    protected void verifyExpectations(Expect[] expectations, ResultActions result) throws Exception {
        for (Expect expect : expectations) {
            // Verify status if specified
            if (expect.status() != null) {
                result.andExpect(status().is(expect.status().value()));
            }

            // Verify JSON paths if specified
            String[] jsonPaths = expect.jsonPath();
            String[] values = expect.value();

            if (jsonPaths != null && jsonPaths.length > 0) {
                for (int i = 0; i < jsonPaths.length; i++) {
                    String path = jsonPaths[i];
                    if (i < values.length) {
                        result.andExpect(MockMvcResultMatchers.jsonPath(path).value(values[i]));
                    }
                }
            }

            // Verify existence checks
            if (expect.exists() != null && expect.exists().length > 0) {
                for (String path : expect.exists()) {
                    result.andExpect(MockMvcResultMatchers.jsonPath(path).exists());
                }
            }

            // Verify non-existence checks
            if (expect.notExists() != null && expect.notExists().length > 0) {
                for (String path : expect.notExists()) {
                    result.andExpect(MockMvcResultMatchers.jsonPath(path).doesNotExist());
                }
            }

            // Verify null checks
            if (expect.isNull() != null && expect.isNull().length > 0) {
                for (String path : expect.isNull()) {
                    result.andExpect(MockMvcResultMatchers.jsonPath(path).isEmpty());
                }
            }

            // Verify not-null checks
            if (expect.notNull() != null && expect.notNull().length > 0) {
                for (String path : expect.notNull()) {
                    result.andExpect(MockMvcResultMatchers.jsonPath(path).isNotEmpty());
                }
            }

            // Verify error message if specified
            if (expect.error() != null && !expect.error().isEmpty()) {
                result.andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expect.error()));
            }
        }
    }

    protected abstract String getBasePath();

    /**
     * Build an entity from the current test method's scenario
     * @return Entity built according to the test scenario
     */
    protected T buildFromCurrentScenario() {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        try {
            Method method = getClass().getMethod(methodName);
            return builder.buildFromTestMethod(method);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find test method: " + methodName, e);
        }
    }

    /**
     * Get metadata about the current test scenario
     * @return Metadata about the current test's scenario
     */
    protected TestEntityBuilder.TestScenarioMetadata getCurrentScenarioMetadata() {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        try {
            Method method = getClass().getMethod(methodName);
            return builder.getTestScenarioMetadata(method)
                .orElseThrow(() -> new IllegalStateException("No @TestScenario annotation found on method: " + methodName));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Could not find test method: " + methodName, e);
        }
    }

    /**
     * Check if the current test method has a specific scenario
     * @param scenarioName Name of the scenario to check for
     * @return true if the current test uses the specified scenario
     */
    protected boolean isScenario(String scenarioName) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        try {
            Method method = getClass().getMethod(methodName);
            TestScenario scenario = method.getAnnotation(TestScenario.class);
            return scenario != null && scenario.value().equals(scenarioName);
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    protected Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

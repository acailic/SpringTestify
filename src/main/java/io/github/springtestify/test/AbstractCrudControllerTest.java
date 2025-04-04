package io.github.springtestify.test;

import io.github.springtestify.annotation.CrudControllerTest;
import io.github.springtestify.generator.EntityGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base class for testing CRUD controllers
 * @param <T> Entity type
 * @param <ID> Entity ID type
 * @param <S> Service type
 */
@AutoConfigureMockMvc
public abstract class AbstractCrudControllerTest<T, ID, S extends CrudService<T, ID>> {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected S service;

    protected final EntityGenerator entityGenerator = new EntityGenerator();
    private final Class<T> entityClass;
    private String basePath;

    @SuppressWarnings("unchecked")
    public AbstractCrudControllerTest() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
        initBasePath();
    }

    private void initBasePath() {
        CrudControllerTest annotation = AnnotationUtils.findAnnotation(getClass(), CrudControllerTest.class);
        if (annotation != null) {
            this.basePath = annotation.path();
        } else {
            throw new IllegalStateException("@CrudControllerTest annotation is required");
        }
    }

    @BeforeEach
    void setUp() {
        setupMocks();
    }

    protected void setupMocks() {
        T testEntity = createTestEntity();
        when(service.findById(any())).thenReturn(Optional.of(testEntity));
        when(service.save(any())).thenReturn(testEntity);
        when(service.findAll(any(Pageable.class))).thenReturn(
            new PageImpl<>(Arrays.asList(testEntity), Pageable.ofSize(10), 1)
        );
    }

    protected abstract T createTestEntity();
    protected abstract ID getTestEntityId();

    protected String getBasePath() {
        return this.basePath;
    }

    @Test
    void shouldReturnPagedEntities() throws Exception {
        // Given
        int totalElements = 20;
        Page<T> page = entityGenerator.generatePage(
            entityClass,
            Pageable.ofSize(10),
            totalElements,
            i -> createTestEntity()
        );
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        // When/Then
        performGet("?page=0&size=10")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").value(totalElements));
    }

    @Test
    void shouldReturnEntityById() throws Exception {
        // When/Then
        performGet("/" + getTestEntityId())
            .andExpect(status().isOk())
            .andExpectAll(getCommonEntityMatchers());
    }

    @Test
    void shouldReturnNotFoundForNonExistentEntity() throws Exception {
        // Given
        when(service.findById(any())).thenReturn(Optional.empty());

        // When/Then
        performGet("/" + getTestEntityId())
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateEntity() throws Exception {
        // Given
        T entity = createTestEntity();

        // When/Then
        performPost("")
            .withContent(objectMapper.writeValueAsString(entity))
            .andExpect(status().isCreated())
            .andExpectAll(getCommonEntityMatchers());
    }

    @Test
    void shouldUpdateEntity() throws Exception {
        // Given
        T entity = createTestEntity();

        // When/Then
        performPut("/" + getTestEntityId())
            .withContent(objectMapper.writeValueAsString(entity))
            .andExpect(status().isOk())
            .andExpectAll(getCommonEntityMatchers());
    }

    @Test
    void shouldDeleteEntity() throws Exception {
        performDelete("/" + getTestEntityId())
            .andExpect(status().isNoContent());
    }

    protected ResultActions performGet(String urlTemplate) throws Exception {
        return mockMvc.perform(get(getBasePath() + urlTemplate)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performPost(String urlTemplate) throws Exception {
        return mockMvc.perform(post(getBasePath() + urlTemplate)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performPut(String urlTemplate) throws Exception {
        return mockMvc.perform(put(getBasePath() + urlTemplate)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performDelete(String urlTemplate) throws Exception {
        return mockMvc.perform(delete(getBasePath() + urlTemplate)
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultMatcher[] getCommonEntityMatchers() {
        return new ResultMatcher[0]; // Override to add common field assertions
    }

    protected void assertField(String fieldName, Object expectedValue) throws Exception {
        performGet("/" + getTestEntityId())
            .andExpect(jsonPath("$." + fieldName).value(expectedValue));
    }

    protected void assertFields(Object entity) throws Exception {
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (value != null) {
                assertField(field.getName(), value);
            }
        }
    }
}

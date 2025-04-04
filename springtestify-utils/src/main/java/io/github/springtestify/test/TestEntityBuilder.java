package io.github.springtestify.test;

import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Generic builder for creating test entities with dynamic field values.
 * @param <T> The type of entity to build
 */
public class TestEntityBuilder<T> {
    private final Class<T> entityClass;
    private final Map<String, Supplier<?>> fieldGenerators;

    public TestEntityBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.fieldGenerators = new HashMap<>();
    }

    /**
     * Add a field generator to the builder
     * @param name Field name
     * @param generator Supplier that generates the field value
     * @return this builder instance
     */
    public TestEntityBuilder<T> withField(String name, Supplier<?> generator) {
        fieldGenerators.put(name, generator);
        return this;
    }

    /**
     * Build the entity with all configured field values
     * @return the constructed entity
     */
    public T build() {
        try {
            T entity = entityClass.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Supplier<?>> entry : fieldGenerators.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue().get();

                Field field = ReflectionUtils.findField(entityClass, fieldName);
                if (field != null) {
                    field.setAccessible(true);
                    field.set(entity, value);
                }
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build entity: " + e.getMessage(), e);
        }
    }

    /**
     * Create a copy of this builder
     * @return a new builder with the same configuration
     */
    public TestEntityBuilder<T> copy() {
        TestEntityBuilder<T> copy = new TestEntityBuilder<>(entityClass);
        copy.fieldGenerators.putAll(this.fieldGenerators);
        return copy;
    }

    /**
     * Add or update multiple fields at once
     * @param fields Map of field names to their generators
     * @return this builder instance
     */
    public TestEntityBuilder<T> withFields(Map<String, Supplier<?>> fields) {
        fieldGenerators.putAll(fields);
        return this;
    }
}

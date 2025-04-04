package io.github.springtestify.test;

import java.lang.reflect.Field;
import java.util.Arrays;
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

                Field field = findField(entityClass, fieldName);
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
     * Find a field in the class hierarchy
     * @param clazz The class to search
     * @param fieldName The name of the field
     * @return The field if found, null otherwise
     */
    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            try {
                return searchType.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                searchType = searchType.getSuperclass();
            }
        }
        return null;
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

    /**
     * Set the ID field of an entity
     * @param id The ID value
     * @return this builder instance
     */
    public TestEntityBuilder<T> withId(Object id) {
        return withField("id", () -> id);
    }

    /**
     * Get all declared fields including inherited ones
     * @return Array of all fields
     */
    public Field[] getAllFields() {
        return getAllFields(new HashMap<>(), entityClass).values().toArray(new Field[0]);
    }

    private Map<String, Field> getAllFields(Map<String, Field> fields, Class<?> type) {
        if (type != null) {
            // Add declared fields from current class
            Arrays.stream(type.getDeclaredFields())
                  .forEach(field -> fields.putIfAbsent(field.getName(), field));

            // Recursively get fields from superclass
            getAllFields(fields, type.getSuperclass());
        }
        return fields;
    }
}

package io.github.springtestify.data.generator;

import java.util.List;
import java.util.Map;

/**
 * Interface defining methods for generating test data for entities.
 * <p>
 * Implementations of this interface can generate single or multiple
 * instances of entities with random or specified property values.
 */
public interface TestDataGenerator {
    
    /**
     * Generates a single entity instance with random property values.
     *
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @return a new entity instance
     */
    <T> T generateOne(Class<T> entityClass);
    
    /**
     * Generates a single entity instance with specified property values.
     *
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @param propertyValues a map of property names to values
     * @return a new entity instance
     */
    <T> T generateOne(Class<T> entityClass, Map<String, Object> propertyValues);
    
    /**
     * Generates multiple entity instances with random property values.
     *
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @param count the number of entities to generate
     * @return a list of entity instances
     */
    <T> List<T> generate(Class<T> entityClass, int count);
    
    /**
     * Generates multiple entity instances with specified property values.
     *
     * @param <T> the entity type
     * @param entityClass the class of the entity
     * @param count the number of entities to generate
     * @param propertyValues a map of property names to values or value distributions
     * @return a list of entity instances
     */
    <T> List<T> generate(Class<T> entityClass, int count, Map<String, String> propertyValues);
    
    /**
     * Saves the generated entities to the database.
     *
     * @param <T> the entity type
     * @param entities the entities to save
     * @return the saved entities
     */
    <T> List<T> saveAll(List<T> entities);
}
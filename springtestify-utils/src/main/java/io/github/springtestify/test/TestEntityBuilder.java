package io.github.springtestify.test;

import io.github.springtestify.annotation.TestEntity;
import io.github.springtestify.annotation.TestField;
import io.github.springtestify.annotation.TestScenario;
import io.github.springtestify.generator.EntityGenerator;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Generic builder for creating test entities with dynamic field values.
 * @param <T> The type of entity to build
 */
public class TestEntityBuilder<T> {
    private final Class<T> entityClass;
    private final Map<String, Supplier<?>> fieldGenerators;
    private final EntityGenerator entityGenerator;
    private final TestEntity testEntity;
    private final Map<String, TestEntity.Scenario> scenarios;

    public TestEntityBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.fieldGenerators = new HashMap<>();
        this.entityGenerator = new EntityGenerator();
        this.testEntity = entityClass.getAnnotation(TestEntity.class);
        this.scenarios = initScenarios();
        processAnnotations();
    }

    private Map<String, TestEntity.Scenario> initScenarios() {
        Map<String, TestEntity.Scenario> scenarioMap = new HashMap<>();
        if (testEntity != null) {
            for (TestEntity.Scenario scenario : testEntity.scenarios()) {
                scenarioMap.put(scenario.name(), scenario);
            }
        }
        return scenarioMap;
    }

    /**
     * Create an entity using a predefined scenario
     * @param scenarioName Name of the scenario to use
     * @return Built entity with scenario values
     */
    public T buildScenario(String scenarioName) {
        TestEntity.Scenario scenario = scenarios.get(scenarioName);
        if (scenario == null) {
            throw new IllegalArgumentException("Unknown scenario: " + scenarioName);
        }

        TestEntityBuilder<T> builder = this.copy();
        for (TestEntity.FieldValue fieldValue : scenario.values()) {
            builder.withField(fieldValue.field(), () -> convertValue(fieldValue.value(),
                findField(entityClass, fieldValue.field()).getType()));
        }
        return builder.build();
    }

    /**
     * Process annotations on the entity class to set up field generators
     */
    private void processAnnotations() {
        // Process class-level defaults
        if (testEntity != null && testEntity.autoGenerateId()) {
            withId(convertValue(testEntity.defaultId(), findField(entityClass, "id").getType()));
        }

        // Process field-level annotations
        for (Field field : getAllFields()) {
            TestField testField = field.getAnnotation(TestField.class);
            if (testField != null) {
                setupFieldGenerator(field, testField);
            }
        }
    }

    private void setupFieldGenerator(Field field, TestField testField) {
        if (!testField.value().isEmpty()) {
            // Use static value
            fieldGenerators.put(field.getName(), () -> convertValue(testField.value(), field.getType()));
        } else if (!testField.generator().isEmpty()) {
            // Use named generator
            fieldGenerators.put(field.getName(), getGeneratorByName(testField.generator()));
        } else {
            // Use default generator based on type
            fieldGenerators.put(field.getName(), getDefaultGenerator(field.getType()));
        }
    }

    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
        if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value);
        if (targetType.isEnum()) return Enum.valueOf((Class<Enum>) targetType, value);
        throw new IllegalArgumentException("Unsupported type for static value conversion: " + targetType);
    }

    private Supplier<?> getGeneratorByName(String name) {
        Supplier<?> generator;
        switch (name) {
            case "email":
                generator = EntityGenerator.CommonGenerators::email;
                break;
            case "name":
                generator = EntityGenerator.CommonGenerators::name;
                break;
            case "uuid":
                generator = EntityGenerator.CommonGenerators::uuid;
                break;
            default:
                throw new IllegalArgumentException("Unknown generator: " + name);
        }
        return generator;
    }

    private Supplier<?> getDefaultGenerator(Class<?> type) {
        Supplier<?> generator;
        if (type == String.class) {
            generator = EntityGenerator.CommonGenerators::randomString;
        } else if (type == Integer.class || type == int.class) {
            generator = () -> entityGenerator.randomInt(1, 1000);
        } else if (type == Long.class || type == long.class) {
            generator = () -> entityGenerator.randomLong(1, 1000);
        } else if (type == Boolean.class || type == boolean.class) {
            generator = () -> entityGenerator.randomBoolean();
        } else if (type.isEnum()) {
            Object[] enumConstants = type.getEnumConstants();
            generator = () -> enumConstants[entityGenerator.randomInt(0, enumConstants.length - 1)];
        } else {
            throw new IllegalArgumentException("No default generator for type: " + type);
        }
        return generator;
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

    /**
     * Get available scenario names for this entity
     * @return Array of scenario names
     */
    public String[] getAvailableScenarios() {
        return scenarios.keySet().toArray(new String[0]);
    }

    /**
     * Check if a scenario exists
     * @param scenarioName Name of the scenario
     * @return true if scenario exists
     */
    public boolean hasScenario(String scenarioName) {
        return scenarios.containsKey(scenarioName);
    }

    /**
     * Get scenario by name
     * @param scenarioName Name of the scenario
     * @return Optional containing the scenario if it exists
     */
    public Optional<TestEntity.Scenario> getScenario(String scenarioName) {
        return Optional.ofNullable(scenarios.get(scenarioName));
    }

    /**
     * Create an entity using a test method's @TestScenario annotation
     * @param testMethod The test method containing the @TestScenario annotation
     * @return Built entity with scenario values and overrides
     */
    public T buildFromTestMethod(Method testMethod) {
        TestScenario testScenario = testMethod.getAnnotation(TestScenario.class);
        if (testScenario == null) {
            throw new IllegalArgumentException("Test method does not have @TestScenario annotation");
        }

        TestEntityBuilder<T> builder = this.copy();

        // Apply base scenario
        TestEntity.Scenario scenario = scenarios.get(testScenario.value());
        if (scenario == null) {
            throw new IllegalArgumentException("Unknown scenario: " + testScenario.value());
        }

        // Apply scenario values
        for (TestEntity.FieldValue fieldValue : scenario.values()) {
            builder.withField(fieldValue.field(), () -> convertValue(fieldValue.value(),
                findField(entityClass, fieldValue.field()).getType()));
        }

        // Apply overrides
        for (TestEntity.FieldValue override : testScenario.overrides()) {
            builder.withField(override.field(), () -> convertValue(override.value(),
                findField(entityClass, override.field()).getType()));
        }

        return builder.build();
    }

    /**
     * Get test scenario metadata for a test method
     * @param testMethod The test method
     * @return Optional containing scenario metadata if present
     */
    public Optional<TestScenarioMetadata> getTestScenarioMetadata(Method testMethod) {
        TestScenario annotation = testMethod.getAnnotation(TestScenario.class);
        if (annotation == null) {
            return Optional.empty();
        }

        return Optional.of(new TestScenarioMetadata(
            annotation.value(),
            annotation.description(),
            annotation.expected(),
            annotation.overrides()
        ));
    }

    /**
     * Metadata class for test scenarios
     */
    public static class TestScenarioMetadata {
        private final String scenarioName;
        private final String description;
        private final String expectedResult;
        private final TestEntity.FieldValue[] overrides;

        public TestScenarioMetadata(
            String scenarioName,
            String description,
            String expectedResult,
            TestEntity.FieldValue[] overrides
        ) {
            this.scenarioName = scenarioName;
            this.description = description;
            this.expectedResult = expectedResult;
            this.overrides = overrides;
        }

        public String scenarioName() {
            return scenarioName;
        }

        public String description() {
            return description;
        }

        public String expectedResult() {
            return expectedResult;
        }

        public TestEntity.FieldValue[] overrides() {
            return overrides;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestScenarioMetadata that = (TestScenarioMetadata) o;
            return Objects.equals(scenarioName, that.scenarioName) &&
                   Objects.equals(description, that.description) &&
                   Objects.equals(expectedResult, that.expectedResult) &&
                   Arrays.equals(overrides, that.overrides);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(scenarioName, description, expectedResult);
            result = 31 * result + Arrays.hashCode(overrides);
            return result;
        }

        @Override
        public String toString() {
            return "TestScenarioMetadata{" +
                   "scenarioName='" + scenarioName + '\'' +
                   ", description='" + description + '\'' +
                   ", expectedResult='" + expectedResult + '\'' +
                   ", overrides=" + Arrays.toString(overrides) +
                   '}';
        }
    }
}

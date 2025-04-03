package io.github.springtestify.data.generator;

import com.github.javafaker.Faker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link TestDataGenerator} using JavaFaker to generate random data.
 * <p>
 * This implementation can generate data for entities with primitive types, common Java types,
 * and simple relationships between entities.
 */
public class DefaultTestDataGenerator implements TestDataGenerator {

    private final Faker faker = new Faker();
    
    @Autowired(required = false)
    private Map<Class<?>, CrudRepository<?, ?>> repositories;

    @Override
    public <T> T generateOne(Class<T> entityClass) {
        return generateOne(entityClass, Collections.emptyMap());
    }

    @Override
    public <T> T generateOne(Class<T> entityClass, Map<String, Object> propertyValues) {
        T entity;
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + entityClass.getName(), e);
        }

        BeanWrapper wrapper = new BeanWrapperImpl(entity);
        
        // Set properties with random values
        for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
            String propertyName = descriptor.getName();
            
            // Skip "class" property
            if (propertyName.equals("class")) {
                continue;
            }
            
            // Use provided value if available
            if (propertyValues.containsKey(propertyName)) {
                wrapper.setPropertyValue(propertyName, propertyValues.get(propertyName));
                continue;
            }
            
            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();
            
            // Skip if no setter
            if (writeMethod == null) {
                continue;
            }
            
            Class<?> propertyType = descriptor.getPropertyType();
            
            // Generate random value for the property
            Object value = generateRandomValueForType(propertyType, propertyName);
            
            if (value != null) {
                try {
                    wrapper.setPropertyValue(propertyName, value);
                } catch (Exception e) {
                    // Just skip if we can't set this property
                }
            }
        }
        
        return entity;
    }

    @Override
    public <T> List<T> generate(Class<T> entityClass, int count) {
        return generate(entityClass, count, Collections.emptyMap());
    }

    @Override
    public <T> List<T> generate(Class<T> entityClass, int count, Map<String, String> propertyValues) {
        List<T> entities = new ArrayList<>(count);
        
        // Process property distributions
        Map<String, List<Object>> propertyDistributions = processPropertyDistributions(propertyValues);
        
        for (int i = 0; i < count; i++) {
            // Create property values for this entity based on distributions
            Map<String, Object> entityPropertyValues = new HashMap<>();
            
            for (Map.Entry<String, List<Object>> entry : propertyDistributions.entrySet()) {
                String propertyName = entry.getKey();
                List<Object> distribution = entry.getValue();
                
                if (!distribution.isEmpty()) {
                    // Select a value from the distribution
                    Object value = distribution.get(i % distribution.size());
                    entityPropertyValues.put(propertyName, value);
                }
            }
            
            // Generate the entity
            T entity = generateOne(entityClass, entityPropertyValues);
            entities.add(entity);
        }
        
        return entities;
    }

    @Override
    public <T> List<T> saveAll(List<T> entities) {
        if (entities.isEmpty() || repositories == null) {
            return entities;
        }
        
        Class<?> entityClass = entities.get(0).getClass();
        CrudRepository<T, ?> repository = (CrudRepository<T, ?>) repositories.get(entityClass);
        
        if (repository != null) {
            return (List<T>) repository.saveAll(entities);
        }
        
        return entities;
    }
    
    /**
     * Processes property distributions from string specifications.
     * <p>
     * For example, "status=NEW:3,IN_PROGRESS:2,COMPLETED:1" would create a distribution
     * with 3 NEW, 2 IN_PROGRESS, and 1 COMPLETED.
     *
     * @param propertyValues the property value specifications
     * @return a map of property names to distribution lists
     */
    private Map<String, List<Object>> processPropertyDistributions(Map<String, String> propertyValues) {
        Map<String, List<Object>> distributions = new HashMap<>();
        
        for (Map.Entry<String, String> entry : propertyValues.entrySet()) {
            String propertyName = entry.getKey();
            String valueSpec = entry.getValue();
            
            List<Object> distribution = parseValueDistribution(valueSpec);
            distributions.put(propertyName, distribution);
        }
        
        return distributions;
    }
    
    /**
     * Parses a value distribution specification.
     *
     * @param valueSpec the value specification string
     * @return a list of values according to the distribution
     */
    private List<Object> parseValueDistribution(String valueSpec) {
        List<Object> result = new ArrayList<>();
        
        // Check if it's a range (for numeric types)
        if (valueSpec.contains(":") && !valueSpec.contains(",")) {
            String[] parts = valueSpec.split(":");
            if (parts.length == 2) {
                try {
                    // For now, only support numeric ranges
                    double min = Double.parseDouble(parts[0]);
                    double max = Double.parseDouble(parts[1]);
                    
                    // Just add min and max as samples for now
                    result.add(min);
                    result.add(max);
                    
                    return result;
                } catch (NumberFormatException e) {
                    // Not a numeric range, continue with other parsing
                }
            }
        }
        
        // Parse value distribution with counts, e.g. "NEW:3,IN_PROGRESS:2"
        String[] valueParts = valueSpec.split(",");
        
        for (String valuePart : valueParts) {
            String[] countSpec = valuePart.split(":");
            String value = countSpec[0].trim();
            int count = 1;
            
            if (countSpec.length > 1) {
                try {
                    count = Integer.parseInt(countSpec[1].trim());
                } catch (NumberFormatException e) {
                    // Ignore and use default count of 1
                }
            }
            
            // Add the value to the result the specified number of times
            for (int i = 0; i < count; i++) {
                result.add(convertStringToAppropriateType(value));
            }
        }
        
        return result;
    }
    
    /**
     * Converts a string to an appropriate type based on its format.
     *
     * @param value the string value to convert
     * @return the converted value
     */
    private Object convertStringToAppropriateType(String value) {
        // Try to parse as a number
        if (value.matches("-?\\d+")) {
            return Integer.parseInt(value);
        }
        if (value.matches("-?\\d+\\.\\d+")) {
            return Double.parseDouble(value);
        }
        // It's a string
        return value;
    }
    
    /**
     * Generates a random value for the specified type and property name.
     *
     * @param type the property type
     * @param propertyName the property name (used for heuristics)
     * @return a random value of the appropriate type
     */
    private Object generateRandomValueForType(Class<?> type, String propertyName) {
        if (type == String.class) {
            return generateStringForProperty(propertyName);
        } else if (type == Integer.class || type == int.class) {
            return faker.number().numberBetween(1, 1000);
        } else if (type == Long.class || type == long.class) {
            return faker.number().numberBetween(1L, 10000L);
        } else if (type == Double.class || type == double.class) {
            return faker.number().randomDouble(2, 1, 1000);
        } else if (type == Float.class || type == float.class) {
            return (float) faker.number().randomDouble(2, 1, 1000);
        } else if (type == Boolean.class || type == boolean.class) {
            return faker.bool().bool();
        } else if (type == Date.class) {
            return faker.date().past(30, TimeUnit.DAYS);
        } else if (type == LocalDate.class) {
            return faker.date().past(30, TimeUnit.DAYS).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        } else if (type == LocalDateTime.class) {
            return faker.date().past(30, TimeUnit.DAYS).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else if (type.isEnum()) {
            Object[] enumConstants = type.getEnumConstants();
            return enumConstants[faker.number().numberBetween(0, enumConstants.length)];
        } else if (Collection.class.isAssignableFrom(type)) {
            return Collections.emptyList(); // For collections, just return empty for now
        } else if (Map.class.isAssignableFrom(type)) {
            return Collections.emptyMap(); // For maps, just return empty for now
        } else if (type.isAnnotationPresent(Entity.class)) {
            return generateOne(type); // For entity references, generate a new instance
        }
        
        return null;
    }
    
    /**
     * Generates a string value appropriate for the property name based on heuristics.
     *
     * @param propertyName the property name
     * @return a string value appropriate for the property
     */
    private String generateStringForProperty(String propertyName) {
        String lowerName = propertyName.toLowerCase();
        
        if (lowerName.contains("name")) {
            if (lowerName.contains("first")) {
                return faker.name().firstName();
            } else if (lowerName.contains("last")) {
                return faker.name().lastName();
            } else if (lowerName.contains("full")) {
                return faker.name().fullName();
            } else {
                return faker.name().name();
            }
        } else if (lowerName.contains("email")) {
            return faker.internet().emailAddress();
        } else if (lowerName.contains("phone")) {
            return faker.phoneNumber().phoneNumber();
        } else if (lowerName.contains("address")) {
            if (lowerName.contains("street")) {
                return faker.address().streetAddress();
            } else if (lowerName.contains("city")) {
                return faker.address().city();
            } else if (lowerName.contains("state")) {
                return faker.address().state();
            } else if (lowerName.contains("country")) {
                return faker.address().country();
            } else if (lowerName.contains("zip") || lowerName.contains("postal")) {
                return faker.address().zipCode();
            } else {
                return faker.address().fullAddress();
            }
        } else if (lowerName.contains("desc")) {
            return faker.lorem().paragraph();
        } else if (lowerName.contains("title")) {
            return StringUtils.capitalize(faker.lorem().words(3).stream().collect(Collectors.joining(" ")));
        } else if (lowerName.contains("url") || lowerName.contains("website")) {
            return faker.internet().url();
        } else if (lowerName.contains("username")) {
            return faker.name().username();
        } else if (lowerName.contains("password")) {
            return faker.internet().password();
        } else if (lowerName.contains("id") && !lowerName.equals("id")) {
            return UUID.randomUUID().toString();
        }
        
        // Default case
        return faker.lorem().word();
    }
}
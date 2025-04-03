package io.github.springtestify.data.generator;

import com.github.javafaker.Faker;
import io.github.springtestify.core.annotation.FakerData;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Test data generator that uses Java Faker to generate realistic test data.
 */
public class FakerTestDataGenerator {
    private final Map<String, Faker> fakerInstances = new HashMap<>();

    /**
     * Generates test data using Faker based on the provided annotation.
     *
     * @param fakerData The FakerData annotation containing generation configuration
     * @return List of generated entities
     */
    public List<Object> generateData(FakerData fakerData) {
        List<Object> generatedEntities = new ArrayList<>();
        Faker faker = getFaker(fakerData.locale());

        for (int i = 0; i < fakerData.count(); i++) {
            Object entity = generateEntity(fakerData.entity(), fakerData.fields(), faker);
            generatedEntities.add(entity);
        }

        return generatedEntities;
    }

    private Object generateEntity(Class<?> entityClass, String[] fieldMappings, Faker faker) {
        try {
            Object entity = entityClass.getDeclaredConstructor().newInstance();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entityClass);

            for (String mapping : fieldMappings) {
                String[] parts = mapping.split("=", 2);
                if (parts.length != 2) continue;

                String fieldName = parts[0].trim();
                String fakerExpression = parts[1].trim();

                setPropertyValue(entity, fieldName, fakerExpression, faker, propertyDescriptors);
            }

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate entity using Faker", e);
        }
    }

    private void setPropertyValue(Object entity, String fieldName, String fakerExpression,
                                Faker faker, PropertyDescriptor[] propertyDescriptors) {
        try {
            PropertyDescriptor descriptor = findPropertyDescriptor(propertyDescriptors, fieldName);
            if (descriptor == null || descriptor.getWriteMethod() == null) {
                return;
            }

            Object value = evaluateFakerExpression(faker, fakerExpression);
            descriptor.getWriteMethod().invoke(entity, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set property " + fieldName, e);
        }
    }

    private Object evaluateFakerExpression(Faker faker, String expression) {
        try {
            String[] parts = expression.split("\\.");
            Object current = faker;

            for (String part : parts) {
                if (current == null) break;
                Method method = current.getClass().getMethod(part);
                current = method.invoke(current);
            }

            return current;
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate Faker expression: " + expression, e);
        }
    }

    private PropertyDescriptor findPropertyDescriptor(PropertyDescriptor[] descriptors, String propertyName) {
        return Arrays.stream(descriptors)
                .filter(pd -> pd.getName().equals(propertyName))
                .findFirst()
                .orElse(null);
    }

    private Faker getFaker(String locale) {
        if (!StringUtils.hasText(locale)) {
            return new Faker();
        }
        return fakerInstances.computeIfAbsent(locale,
            key -> new Faker(new Locale(locale)));
    }
}

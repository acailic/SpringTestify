package io.github.springtestify.data.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building property value specifications for {@code @GenerateTestData}.
 * <p>
 * This class provides a fluent API for defining property values and distributions,
 * which can be used in the {@code properties} attribute of the annotation.
 * <p>
 * Example usage:
 * <pre>
 * &#064;GenerateTestData(
 *     entity = User.class, 
 *     count = 10, 
 *     properties = {
 *         PropertyValueBuilder.property("role").value("ADMIN").count(2)
 *                             .value("USER").count(8).build(),
 *         PropertyValueBuilder.property("active").value(true).build()
 *     }
 * )
 * </pre>
 */
public class PropertyValueBuilder {

    private final String propertyName;
    private final List<ValueCount> valueCounts = new ArrayList<>();

    private PropertyValueBuilder(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Creates a new builder for the specified property.
     *
     * @param propertyName the property name
     * @return the builder
     */
    public static PropertyValueBuilder property(String propertyName) {
        return new PropertyValueBuilder(propertyName);
    }

    /**
     * Adds a value to the property.
     *
     * @param value the value
     * @return a value builder to specify the count
     */
    public ValueBuilder value(Object value) {
        return new ValueBuilder(this, value);
    }

    /**
     * Adds a range of numeric values to the property.
     *
     * @param min the minimum value
     * @param max the maximum value
     * @return the builder
     */
    public PropertyValueBuilder range(double min, double max) {
        return addValueCount(min + ":" + max, 1);
    }

    /**
     * Builds the property value specification string.
     *
     * @return the property value specification
     */
    public String build() {
        StringBuilder result = new StringBuilder();
        result.append(propertyName).append("=");

        boolean first = true;
        for (ValueCount valueCount : valueCounts) {
            if (!first) {
                result.append(",");
            }
            result.append(valueCount.value);
            if (valueCount.count > 1) {
                result.append(":").append(valueCount.count);
            }
            first = false;
        }

        return result.toString();
    }

    private PropertyValueBuilder addValueCount(String value, int count) {
        valueCounts.add(new ValueCount(value, count));
        return this;
    }

    /**
     * Builder for specifying the count of a value.
     */
    public class ValueBuilder {
        private final PropertyValueBuilder parent;
        private final Object value;

        private ValueBuilder(PropertyValueBuilder parent, Object value) {
            this.parent = parent;
            this.value = value;
        }

        /**
         * Specifies the count of this value.
         *
         * @param count the count
         * @return the parent builder
         */
        public PropertyValueBuilder count(int count) {
            return parent.addValueCount(value.toString(), count);
        }

        /**
         * Finishes specifying this value with a count of 1.
         *
         * @return the parent builder
         */
        public PropertyValueBuilder single() {
            return count(1);
        }

        /**
         * Builds the property value specification without specifying a count,
         * which defaults to 1.
         *
         * @return the property value specification
         */
        public String build() {
            return parent.addValueCount(value.toString(), 1).build();
        }
    }

    private static class ValueCount {
        private final String value;
        private final int count;

        private ValueCount(String value, int count) {
            this.value = value;
            this.count = count;
        }
    }
}
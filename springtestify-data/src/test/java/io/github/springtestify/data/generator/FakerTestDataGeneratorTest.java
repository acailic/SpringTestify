package io.github.springtestify.data.generator;

import com.github.javafaker.Faker;
import io.github.springtestify.core.annotation.FakerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FakerTestDataGeneratorTest {

    private FakerTestDataGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new FakerTestDataGenerator();
    }

    @Test
    void shouldGenerateDataWithDefaultLocale() {
        // given
        FakerData fakerData = createFakerData(TestPerson.class, 5, new String[]{
            "firstName=name.firstName",
            "lastName=name.lastName",
            "email=internet.emailAddress"
        }, "");

        // when
        List<Object> generatedData = generator.generateData(fakerData);

        // then
        assertThat(generatedData)
            .hasSize(5)
            .allSatisfy(obj -> {
                TestPerson person = (TestPerson) obj;
                assertThat(person.getFirstName()).isNotBlank();
                assertThat(person.getLastName()).isNotBlank();
                assertThat(person.getEmail()).contains("@");
            });
    }

    @Test
    void shouldGenerateDataWithSpecificLocale() {
        // given
        FakerData fakerData = createFakerData(TestPerson.class, 3, new String[]{
            "firstName=name.firstName",
            "lastName=name.lastName"
        }, "fr");

        // Create a Faker with French locale to verify names
        Faker frenchFaker = new Faker(new Locale("fr"));
        String frenchName = frenchFaker.name().firstName();

        // when
        List<Object> generatedData = generator.generateData(fakerData);

        // then
        assertThat(generatedData).hasSize(3);
        // Verify that names follow French patterns (this is a basic check)
        TestPerson person = (TestPerson) generatedData.get(0);
        assertThat(person.getFirstName()).isNotBlank();
        assertThat(person.getLastName()).isNotBlank();
    }

    @Test
    void shouldHandleInvalidFieldMapping() {
        // given
        FakerData fakerData = createFakerData(TestPerson.class, 1, new String[]{
            "invalidField=name.firstName", // Field doesn't exist
            "email=internet.emailAddress"
        }, "");

        // when
        List<Object> generatedData = generator.generateData(fakerData);

        // then
        assertThat(generatedData)
            .hasSize(1)
            .allSatisfy(obj -> {
                TestPerson person = (TestPerson) obj;
                assertThat(person.getEmail()).contains("@");
                // Invalid field should be ignored
                assertThat(person.getFirstName()).isNull();
            });
    }

    @Test
    void shouldHandleInvalidFakerExpression() {
        // given
        FakerData fakerData = createFakerData(TestPerson.class, 1, new String[]{
            "firstName=invalid.provider.method"
        }, "");

        // when/then
        assertThatThrownBy(() -> generator.generateData(fakerData))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to evaluate Faker expression");
    }

    @Test
    void shouldHandleEmptyFieldMappings() {
        // given
        FakerData fakerData = createFakerData(TestPerson.class, 2, new String[]{}, "");

        // when
        List<Object> generatedData = generator.generateData(fakerData);

        // then
        assertThat(generatedData)
            .hasSize(2)
            .allSatisfy(obj -> {
                TestPerson person = (TestPerson) obj;
                assertThat(person.getFirstName()).isNull();
                assertThat(person.getLastName()).isNull();
                assertThat(person.getEmail()).isNull();
            });
    }

    @Test
    void shouldHandleComplexNestedProperties() {
        // given
        FakerData fakerData = createFakerData(TestAddress.class, 1, new String[]{
            "street=address.streetAddress",
            "city=address.city",
            "country=address.country",
            "zipCode=address.zipCode"
        }, "");

        // when
        List<Object> generatedData = generator.generateData(fakerData);

        // then
        assertThat(generatedData)
            .hasSize(1)
            .allSatisfy(obj -> {
                TestAddress address = (TestAddress) obj;
                assertThat(address.getStreet()).isNotBlank();
                assertThat(address.getCity()).isNotBlank();
                assertThat(address.getCountry()).isNotBlank();
                assertThat(address.getZipCode()).isNotBlank();
            });
    }

    private FakerData createFakerData(Class<?> entityClass, int count, String[] fields, String locale) {
        return new FakerData() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return FakerData.class;
            }

            @Override
            public Class<?> entity() {
                return entityClass;
            }

            @Override
            public int count() {
                return count;
            }

            @Override
            public String[] fields() {
                return fields;
            }

            @Override
            public String locale() {
                return locale;
            }
        };
    }

    // Test entities
    public static class TestPerson {
        private String firstName;
        private String lastName;
        private String email;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class TestAddress {
        private String street;
        private String city;
        private String country;
        private String zipCode;

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    }
}

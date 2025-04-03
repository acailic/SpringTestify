package io.github.springtestify.data.annotation;

import io.github.springtestify.core.annotation.FakerData;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.data.registry.TestDataRegistry;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringTestify
@FakerData(
    entity = TestUser.class,
    count = 5,
    fields = {
        "username=name.username",
        "email=internet.emailAddress",
        "fullName=name.fullName",
        "phoneNumber=phoneNumber.cellPhone"
    }
)
@FakerData(
    entity = TestCompany.class,
    count = 3,
    locale = "en",
    fields = {
        "name=company.name",
        "industry=company.industry",
        "catchPhrase=company.catchPhrase",
        "address=address.fullAddress"
    }
)
class FakerDataIntegrationTest {

    @Autowired
    private TestDataRegistry testDataRegistry;

    @Test
    void shouldGenerateUsersWithFaker() {
        // when
        List<TestUser> users = testDataRegistry.findAll(TestUser.class);

        // then
        assertThat(users)
            .hasSize(5)
            .allSatisfy(user -> {
                assertThat(user.getUsername()).isNotBlank();
                assertThat(user.getEmail()).contains("@");
                assertThat(user.getFullName()).contains(" "); // Should have space between first and last name
                assertThat(user.getPhoneNumber()).isNotBlank();
            });
    }

    @Test
    void shouldGenerateCompaniesWithFaker() {
        // when
        List<TestCompany> companies = testDataRegistry.findAll(TestCompany.class);

        // then
        assertThat(companies)
            .hasSize(3)
            .allSatisfy(company -> {
                assertThat(company.getName()).isNotBlank();
                assertThat(company.getIndustry()).isNotBlank();
                assertThat(company.getCatchPhrase()).isNotBlank();
                assertThat(company.getAddress()).isNotBlank();
            });
    }

    // Test entities
    @Data
    public static class TestUser {
        private String username;
        private String email;
        private String fullName;
        private String phoneNumber;
    }

    @Data
    public static class TestCompany {
        private String name;
        private String industry;
        private String catchPhrase;
        private String address;
    }
}

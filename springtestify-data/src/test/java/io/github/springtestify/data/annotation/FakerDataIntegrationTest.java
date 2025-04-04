package io.github.springtestify.data.annotation;

import io.github.springtestify.core.annotation.FakerData;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.data.listener.TestDataGenerationListener.GeneratedDataRegistry;
import io.github.springtestify.data.model.TestCompany;
import io.github.springtestify.data.model.TestUser;
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
    private GeneratedDataRegistry testDataRegistry;

    @Test
    void shouldGenerateUsersWithFaker() {
        // when
        List<TestUser> users = (List<TestUser>) testDataRegistry.getData().get(TestUser.class);

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
        List<TestCompany> companies = (List<TestCompany>) testDataRegistry.getData().get(TestCompany.class);

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
}

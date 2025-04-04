package io.github.springtestify.examples.controller;

import io.github.springtestify.annotation.CrudControllerTest;
import io.github.springtestify.examples.model.User;
import io.github.springtestify.examples.service.UserService;
import io.github.springtestify.generator.EntityGenerator;
import io.github.springtestify.test.AbstractCrudControllerTest;
import io.github.springtestify.test.TestEntityBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@CrudControllerTest(path = "/api/users")
class UserControllerTest extends AbstractCrudControllerTest<User, Long, UserService> {

    private final TestEntityBuilder<User> builder;

    public UserControllerTest() {
        this.builder = new TestEntityBuilder<>(User.class)
            .withFields(Map.of(
                "email", EntityGenerator.CommonGenerators::email,
                "name", EntityGenerator.CommonGenerators::name,
                "role", () -> "USER",
                "active", () -> true
            ));
    }

    @Override
    protected User createTestEntity() {
        User user = builder.copy().build();
        user.setId(1L);
        return user;
    }

    @Override
    protected Long getTestEntityId() {
        return 1L;
    }

    @Override
    protected ResultMatcher[] getCommonEntityMatchers() {
        return new ResultMatcher[]{
            jsonPath("$.id").exists(),
            jsonPath("$.email").exists(),
            jsonPath("$.name").exists(),
            jsonPath("$.role").exists(),
            jsonPath("$.active").exists()
        };
    }

    @Test
    void shouldReturnUserWithAdminRole() throws Exception {
        User adminUser = builder.copy()
            .withField("role", () -> "ADMIN")
            .build();
        adminUser.setId(1L);
        assertFields(adminUser);
    }

    @Test
    void shouldReturnInactiveUser() throws Exception {
        User inactiveUser = builder.copy()
            .withField("active", () -> false)
            .build();
        inactiveUser.setId(1L);
        assertFields(inactiveUser);
    }
}

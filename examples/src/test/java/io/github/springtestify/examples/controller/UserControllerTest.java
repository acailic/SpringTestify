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
        return builder.copy()
            .withId(1L)
            .build();
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
        assertFields(builder.copy()
            .withId(1L)
            .withField("role", () -> "ADMIN")
            .build());
    }

    @Test
    void shouldReturnInactiveUser() throws Exception {
        assertFields(builder.copy()
            .withId(1L)
            .withField("active", () -> false)
            .build());
    }
}

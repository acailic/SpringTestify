package io.github.springtestify.examples.controller;

import io.github.springtestify.annotation.*;
import io.github.springtestify.examples.model.User;
import io.github.springtestify.examples.service.UserService;
import io.github.springtestify.test.AbstractCrudControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@CrudControllerTest(path = "/api/users")
@ScenarioTest(User.class)
class UserControllerTest extends AbstractCrudControllerTest<User, Long, UserService> {

    // Default no-arg constructor
    public UserControllerTest() {
        super(User.class);
    }

    // Constructor that takes Class<User> parameter to match the generated test class
    public UserControllerTest(Class<User> entityClass) {
        super(entityClass);
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
    @TestScenario(
        value = "admin",
        description = "Should return a user with admin role",
        expect = {
            @Expect(
                status = HttpStatus.CREATED,
                jsonPath = {"$.role", "$.active"},
                value = {"ADMIN", "true"}
            )
        }
    )
    @ScenarioAction(method = HttpMethod.POST)
    void shouldReturnUserWithAdminRole() {}

    @Test
    @TestScenario(
        value = "inactive",
        description = "Should return an inactive user",
        expect = {
            @Expect(
                status = HttpStatus.CREATED,
                jsonPath = "$.active",
                value = "false"
            )
        }
    )
    @ScenarioAction(method = HttpMethod.POST)
    void shouldReturnInactiveUser() {}

    @Test
    @TestScenario(
        value = "moderator",
        description = "Should return a user with moderator role",
        expect = {
            @Expect(
                status = HttpStatus.CREATED,
                jsonPath = {"$.role", "$.active"},
                value = {"MODERATOR", "true"}
            )
        }
    )
    @ScenarioAction(method = HttpMethod.POST)
    void shouldReturnModeratorUser() {}

    @Test
    @TestScenario(
        value = "admin",
        description = "Should return an inactive admin user",
        expect = {
            @Expect(
                status = HttpStatus.CREATED,
                jsonPath = {"$.role", "$.active"},
                value = {"ADMIN", "false"}
            )
        },
        overrides = {
            @TestEntity.FieldValue(field = "active", value = "false")
        }
    )
    @ScenarioAction(method = HttpMethod.POST)
    void shouldReturnInactiveAdminUser() {}

    @Test
    @TestScenario(
        value = "admin",
        description = "Should validate required fields",
        expect = {
            @Expect(
                status = HttpStatus.BAD_REQUEST,
                error = "Email is required",
                notNull = {"email", "name"},
                exists = {"message", "timestamp"}
            )
        }
    )
    @ScenarioAction(method = HttpMethod.POST)
    void shouldValidateRequiredFields() {}

    @Test
    @TestScenario(
        value = "admin",
        description = "Should get user by ID",
        expect = {
            @Expect(
                status = HttpStatus.OK,
                jsonPath = {"$.id", "$.role"},
                value = {"1", "ADMIN"}
            )
        }
    )
    @ScenarioAction(
        method = HttpMethod.GET,
        path = "/{id}",
        params = @ScenarioAction.RequestParam(name = "id", value = "1")
    )
    void shouldGetUserById() {}
}

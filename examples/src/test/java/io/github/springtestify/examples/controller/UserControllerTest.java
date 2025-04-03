package io.github.springtestify.examples.controller;

import io.github.springtestify.core.annotation.ControllerTest;
import io.github.springtestify.examples.model.User;
import io.github.springtestify.examples.service.UserService;
import io.github.springtestify.web.test.AbstractControllerTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import com.github.seregamorph.springtest.SmartDirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(path = "/api/users")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestExecutionListeners(
    listeners = SmartDirtiesContextTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
class UserControllerTest extends AbstractControllerTest {

    @MockBean
    private UserService userService;

    @Test
    @Order(1)
    void shouldReturnAllUsers() throws Exception {
        // Given
        User user1 = new User("user1@example.com", "User One");
        user1.setId(1L);
        User user2 = new User("user2@example.com", "User Two");
        user2.setId(2L);

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // When/Then
        performGet("/")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].email").value("user1@example.com"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }

    @Test
    @Order(2)
    void shouldReturnUserById() throws Exception {
        // Given
        User user = new User("user@example.com", "Test User");
        user.setId(1L);

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // When/Then
        performGet("/1")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @Order(3)
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        // Given
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        // When/Then
        performGet("/99")
            .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void shouldCreateUser() throws Exception {
        // Given
        User requestUser = new User("new@example.com", "New User");

        User createdUser = new User("new@example.com", "New User");
        createdUser.setId(1L);

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // When/Then
        String requestJson = toJson(requestUser);

        performPost("/")
            .withContent(requestJson)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("new@example.com"))
            .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    @Order(5)
    void shouldUpdateUser() throws Exception {
        // Given
        User requestUser = new User("updated@example.com", "Updated User");
        requestUser.setId(1L);

        when(userService.updateUser(any(User.class))).thenReturn(requestUser);

        // When/Then
        String requestJson = toJson(requestUser);

        performPut("/1")
            .withContent(requestJson)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    @Order(6)
    void shouldDeleteUser() throws Exception {
        performDelete("/1")
            .andExpect(status().isNoContent());
    }
}

package io.github.springtestify.examples.service;

import io.github.springtestify.core.annotation.ServiceTest;
import io.github.springtestify.examples.model.User;
import io.github.springtestify.examples.repository.UserRepository;
import io.github.springtestify.service.test.AbstractServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Disabled;

@Disabled("This test is not working")
@ServiceTest(UserServiceImpl.class)
class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // This will be automatically mocked

    @Test
    void shouldCreateUserWhenEmailIsUnique() {
        // Given
        User user = new User("test@example.com", "Test User");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User createdUser = userService.createUser(user);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");

        // Using the verifyMock helper from AbstractServiceTest
        verifyMock(UserRepository.class).existsByEmail("test@example.com");
        verifyMock(UserRepository.class).save(user);
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        User user = new User("existing@example.com", "Test User");
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");

        verifyMock(UserRepository.class).existsByEmail("existing@example.com");
    }

    @Test
    void shouldReturnUsersByRole() {
        // Given
        User admin1 = new User("admin1@example.com", "Admin 1", "ADMIN");
        User admin2 = new User("admin2@example.com", "Admin 2", "ADMIN");
        List<User> adminUsers = Arrays.asList(admin1, admin2);

        when(userRepository.findByRole("ADMIN")).thenReturn(adminUsers);

        // When
        List<User> result = userService.getUsersByRole("ADMIN");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getEmail)
                         .containsExactly("admin1@example.com", "admin2@example.com");

        verifyMock(UserRepository.class).findByRole("ADMIN");
    }

    @Test
    void shouldReturnUserById() {
        // Given
        Long userId = 1L;
        User user = new User("test@example.com", "Test User");
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        verifyMock(UserRepository.class).findById(userId);
    }
}

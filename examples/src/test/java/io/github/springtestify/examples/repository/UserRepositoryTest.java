package io.github.springtestify.examples.repository;

import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.RepositoryTest;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.examples.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@InMemoryDb(type = DbType.H2)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        User user = new User("test@example.com", "Test User");
        
        // When
        User savedUser = userRepository.save(user);
        
        // Then
        assertThat(savedUser.getId()).isNotNull();
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getEmail()).isEqualTo("test@example.com");
    }
    
    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User("test@example.com", "Test User");
        userRepository.save(user);
        
        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }
    
    @Test
    void shouldFindUsersByRole() {
        // Given
        User admin1 = new User("admin1@example.com", "Admin One", "ADMIN");
        User admin2 = new User("admin2@example.com", "Admin Two", "ADMIN");
        User user1 = new User("user1@example.com", "User One", "USER");
        
        userRepository.save(admin1);
        userRepository.save(admin2);
        userRepository.save(user1);
        
        // When
        List<User> admins = userRepository.findByRole("ADMIN");
        
        // Then
        assertThat(admins).hasSize(2);
        assertThat(admins).extracting(User::getEmail)
                         .containsExactlyInAnyOrder("admin1@example.com", "admin2@example.com");
    }
    
    @Test
    void shouldFindActiveUsers() {
        // Given
        User activeUser = new User("active@example.com", "Active User");
        activeUser.setActive(true);
        
        User inactiveUser = new User("inactive@example.com", "Inactive User");
        inactiveUser.setActive(false);
        
        userRepository.save(activeUser);
        userRepository.save(inactiveUser);
        
        // When
        List<User> activeUsers = userRepository.findByActiveTrue();
        
        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getEmail()).isEqualTo("active@example.com");
    }
    
    @Test
    void shouldCheckIfEmailExists() {
        // Given
        User user = new User("test@example.com", "Test User");
        userRepository.save(user);
        
        // When & Then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }
}
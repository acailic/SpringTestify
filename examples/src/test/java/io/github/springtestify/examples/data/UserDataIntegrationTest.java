package io.github.springtestify.examples.data;

import io.github.springtestify.core.annotation.GenerateTestData;
import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.data.util.PropertyValueBuilder;
import io.github.springtestify.data.util.TestDataRegistry;
import io.github.springtestify.examples.model.User;
import io.github.springtestify.examples.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestify
@InMemoryDb(type = DbType.H2)
@GenerateTestData(
    entity = User.class,
    count = 10,
    properties = {
        "role=ADMIN:2,USER:8",
        "active=true:7,false:3"
    }
)
public class UserDataIntegrationTest {

    @Autowired
    private UserService userService;
    
    @Autowired
    private TestDataRegistry testData;
    
    @Test
    void shouldAccessGeneratedUserData() {
        // When
        List<User> allUsers = userService.getAllUsers();
        
        // Then
        assertThat(allUsers).hasSize(10);
        
        List<User> admins = allUsers.stream()
                                   .filter(user -> "ADMIN".equals(user.getRole()))
                                   .collect(Collectors.toList());
        assertThat(admins).hasSize(2);
        
        List<User> activeUsers = allUsers.stream()
                                       .filter(User::isActive)
                                       .collect(Collectors.toList());
        assertThat(activeUsers).hasSize(7);
    }
    
    @Test
    void shouldFindUsersByRoleUsingTestDataRegistry() {
        // When
        List<User> admins = testData.findAll(User.class, user -> "ADMIN".equals(user.getRole()));
        List<User> users = testData.findAll(User.class, user -> "USER".equals(user.getRole()));
        
        // Then
        assertThat(admins).hasSize(2);
        assertThat(users).hasSize(8);
    }
    
    @Test
    void shouldGetFirstUserByPredicate() {
        // When
        User admin = testData.findOne(User.class, user -> "ADMIN".equals(user.getRole())).orElse(null);
        
        // Then
        assertThat(admin).isNotNull();
        assertThat(admin.getRole()).isEqualTo("ADMIN");
    }
}
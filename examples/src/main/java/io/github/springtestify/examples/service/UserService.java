package io.github.springtestify.examples.service;

import io.github.springtestify.examples.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User createUser(User user);
    
    User updateUser(User user);
    
    Optional<User> getUserById(Long id);
    
    Optional<User> getUserByEmail(String email);
    
    List<User> getAllUsers();
    
    List<User> getUsersByRole(String role);
    
    void deleteUser(Long id);
    
    boolean isEmailTaken(String email);
}
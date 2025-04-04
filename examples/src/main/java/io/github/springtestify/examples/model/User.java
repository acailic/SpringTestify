package io.github.springtestify.examples.model;

import io.github.springtestify.annotation.TestEntity;
import io.github.springtestify.annotation.TestField;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    //@Email
    private String email;

    @TestField(generator = "name")
    private String name;

    @TestField(value = "USER")
    private String role;

    @TestField(value = "true")
    private boolean active;

    public User() {
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.role = "USER";
        this.active = true;
    }

    public User(String email, String name, String role) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}

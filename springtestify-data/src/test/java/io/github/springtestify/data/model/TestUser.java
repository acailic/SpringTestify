package io.github.springtestify.data.model;

import lombok.Data;

@Data
public class TestUser {
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
}

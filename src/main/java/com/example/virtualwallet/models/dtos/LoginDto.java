package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class LoginDto {

    @NotEmpty(message = "Username can't be empty")
    private String username;

    @NotNull(message = "Password can't be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@_$!%*?&])[A-Za-z\\d@_$!%*?&]+$",
            message = "Password must contains at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    public LoginDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
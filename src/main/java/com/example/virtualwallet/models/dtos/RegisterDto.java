package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class RegisterDto extends LoginDto {

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$",
            message = "Invalid email format")
    @NotBlank(message = "Email can't be blank.")
    private String email;

    @NotEmpty(message = "Password confirmation can't be empty")
    @NotBlank(message = "Password can't be blank.")
    private String passwordConfirm;

    @NotEmpty(message = "Phone number can't be empty")
    @NotBlank(message = "Phone number can't be blank.")
    private String phoneNumber;
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.*;

public class RegisterDto extends LoginDto {

    @NotNull(message = "First name can't be empty.")
    @NotBlank(message = "First name can't be blank.")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols.")
    private String firstName;

    @NotNull(message = "Last name can't be empty.")
    @NotBlank(message = "Last name can't be blank.")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols.")
    private String lastName;

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

    public RegisterDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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
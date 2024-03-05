package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.*;

public class RegisterDto {
    @NotEmpty(message = "Username can't be empty")
    private String username;

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

//    @NotEmpty(message = "Password confirmation can't be empty")

    @NotNull(message = "Password can't be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@_$!%*?&])[A-Za-z\\d@_$!%*?&]+$",
            message = "Password must contains at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotEmpty(message = "Phone number can't be empty")
    @NotBlank(message = "Phone number can't be blank.")
    private String phoneNumber;

    @NotEmpty(message = "Currency can't be empty")
    @NotBlank(message = "Currency can't be blank.")
    private String currency;

    @NotEmpty(message = "Profile picture can't be empty")
    @NotBlank(message = "Profile picture can't be blank.")
    private String profilePicture;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordConfirm) {
        this.password = passwordConfirm;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
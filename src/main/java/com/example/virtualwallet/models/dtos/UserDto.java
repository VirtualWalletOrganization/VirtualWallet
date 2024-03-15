package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotNull(message = "First name can't be empty.")
    @NotBlank(message = "First name can't be blank.")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols.")
    private String firstName;

    @NotNull(message = "Last name can't be empty.")
    @NotBlank(message = "Last name can't be blank.")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols.")
    private String lastName;

    @NotNull(message = "Username can't be empty.")
    @NotBlank(message = "Username can't be blank.")
    @Size(min = 4, max = 12, message = "Username should be between 4 and 12 symbols.")
    private String username;

    @NotNull(message = "Email must be unique.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$",
            message = "Invalid email format")
    private String email;

    @NotNull(message = "Password can't be empty.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@_$!%*?&])[A-Za-z\\d@_$!%*?&]+$",
            message = "Password must contains at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    private String phoneNumber;

    private Role role = Role.USER;

    private boolean overdraftEnabled;

    private int overdarftLimit;

    private String profilePicture;

    public UserDto() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isOverdraftEnabled() {
        return overdraftEnabled;
    }

    public void setOverdraftEnabled(boolean overdraftEnabled) {
        this.overdraftEnabled = overdraftEnabled;
    }

    public int getOverdarftLimit() {
        return overdarftLimit;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setOverdarftLimit(int overdarftLimit) {
        this.overdarftLimit = overdarftLimit;
    }
}
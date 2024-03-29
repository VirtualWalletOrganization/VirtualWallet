package com.example.virtualwallet.utils;

import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.UserStatus;

import java.util.Optional;

public class UserFilterOptions {

    private Optional<String> username;
    private Optional<String> firstName;
    private Optional<String> lastName;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<Role> role;
    private Optional<UserStatus> status;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public UserFilterOptions() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public UserFilterOptions(
            String username,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            Role role,
            UserStatus status,
            String sortBy,
            String sortOrder) {
        this.username = Optional.ofNullable(username);
        this.firstName = Optional.ofNullable(firstName);
        this.lastName = Optional.ofNullable(lastName);
        this.email = Optional.ofNullable(email);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
        this.role = Optional.ofNullable(role);
        this.status = Optional.ofNullable(status);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public Optional<String> getLastName() {
        return lastName;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public Optional<Role> getRole() {
        return role;
    }

    public Optional<UserStatus> getStatus() {
        return status;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Optional<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
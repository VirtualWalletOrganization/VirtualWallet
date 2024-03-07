package com.example.virtualwallet.models.dtos;


import com.example.virtualwallet.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class UserResponseDto {

    @JsonIgnore
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Role role = Role.USER;
    private String phoneNumber;

    public UserResponseDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserResponseDto(int id, String firstName, String lastName,
                           String email, Role role, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponseDto userResponseDto = (UserResponseDto) o;
        return id == userResponseDto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
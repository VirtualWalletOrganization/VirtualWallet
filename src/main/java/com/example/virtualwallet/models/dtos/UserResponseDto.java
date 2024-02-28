package com.example.virtualwallet.models.dtos;


import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.enums.Role;
import com.example.virtualwallet.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;
import java.util.Set;

public class UserResponseDto {

    @JsonIgnore
    private int id;
    private String email;
    private Role role = Role.USER;
    private String phoneNumber;

    public UserResponseDto() {
    }

    public UserResponseDto(int id, String email, Role role, String phoneNumber) {
        this.id = id;
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
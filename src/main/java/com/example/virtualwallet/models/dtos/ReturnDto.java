package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReturnDto {

    @JsonIgnore
    private int id;
    private String username;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String phoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

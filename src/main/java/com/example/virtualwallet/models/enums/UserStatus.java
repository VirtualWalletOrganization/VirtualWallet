package com.example.virtualwallet.models.enums;

public enum UserStatus {
    BLOCKED("BLOCKED"),
    ACTIVE("ACTIVE");

    private String name;

    UserStatus(String name) {
        this.name = name;
    }
}

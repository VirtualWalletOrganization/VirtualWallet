package com.example.virtualwallet.models;

public enum Status {
    BLOCKED("BLOCKED"),
    ACTIVE("ACTIVE");

    private String name;

    Status(String name) {
        this.name = name;
    }
}

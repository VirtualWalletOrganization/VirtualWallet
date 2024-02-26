package com.example.virtualwallet.models.enums;

public enum Status {

    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    EXPIRED("EXPIRED"),
    PENDING("PENDING");

    private String name;

    Status(String name) {
        this.name = name;
    }
}
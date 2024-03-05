package com.example.virtualwallet.models.enums;

public enum Status {

    EXPIRED("EXPIRED"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    PENDING("PENDING"),
    PENDING_REQUEST("PENDING_REQUEST"),
    REJECT("REJECT");

    private String name;

    Status(String name) {
        this.name = name;
    }
}
package com.example.virtualwallet.models.enums;

public enum Status {

    DECLINED("DECLINED"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    PENDING("PENDING"),
    PENDING_RECURRING_REQUEST("PENDING_RECURRING_REQUEST"),
    REJECTED("REJECTED");

    private String name;

    Status(String name) {
        this.name = name;
    }
}
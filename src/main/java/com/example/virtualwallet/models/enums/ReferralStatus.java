package com.example.virtualwallet.models.enums;

public enum ReferralStatus {

    COMPLETED("COMPLETED"),
    EXPIRED("EXPIRED"),
    PENDING("PENDING");

    private String name;

    ReferralStatus(String name) {
        this.name = name;
    }
}
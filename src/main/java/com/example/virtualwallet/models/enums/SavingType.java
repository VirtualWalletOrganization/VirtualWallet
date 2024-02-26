package com.example.virtualwallet.models.enums;

public enum SavingType {

    FIXED_DEPOSIT("FIXED_DEPOSIT"),
    CERTIFICATE_DEPOSIT("CD");

    private String name;

    SavingType(String name) {
        this.name = name;
    }
}
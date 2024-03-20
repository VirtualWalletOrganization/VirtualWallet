package com.example.virtualwallet.models.enums;

public enum Frequency {

    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY");

    private String name;

    Frequency(String name) {
        this.name = name;
    }
}
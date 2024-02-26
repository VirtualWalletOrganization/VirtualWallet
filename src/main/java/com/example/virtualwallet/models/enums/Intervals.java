package com.example.virtualwallet.models.enums;

public enum Intervals {

    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY");

    private String name;

    Intervals(String name) {
        this.name = name;
    }
}
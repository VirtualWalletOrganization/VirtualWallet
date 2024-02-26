package com.example.virtualwallet.models.enums;

public enum Interval {

    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY");

    private String name;

    Interval(String name) {
        this.name = name;
    }
}
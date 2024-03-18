package com.example.virtualwallet.models.enums;

public enum TransactionType {
    SINGLE("SINGLE"),
    RECURRING("RECURRING");

    private String name;

    TransactionType(String name) {
        this.name = name;
    }
}
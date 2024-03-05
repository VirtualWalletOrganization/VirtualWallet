package com.example.virtualwallet.models.enums;

public enum TransactionType {

    DUMMY("DUMMY"),
    SINGLE("SINGLE"),
    RECURRING("RECURRING");

    private String name;

    TransactionType(String name) {
        this.name = name;
    }
}
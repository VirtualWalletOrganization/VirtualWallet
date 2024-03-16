package com.example.virtualwallet.models.enums;

public enum CardType {

    CREDIT("CREDIT"),
    DEBIT("DEBIT");

    private String name;

    CardType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
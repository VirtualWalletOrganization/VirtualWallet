package com.example.virtualwallet.models;

public enum CardType {
    CREDIT("CREDIT"),
    DEBIT("DEBIT");
    private String name;

    CardType(String name) {
        this.name = name;
    }
}

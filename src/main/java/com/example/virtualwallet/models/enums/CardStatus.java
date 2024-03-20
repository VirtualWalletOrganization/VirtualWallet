package com.example.virtualwallet.models.enums;

public enum CardStatus {

    ACTIVE("ACTIVE"),
    DEACTIVATED("DEACTIVATED");

    private String name;

    CardStatus(String name) {
        this.name = name;
    }
}
package com.example.virtualwallet.models.enums;

public enum WalletRole {

    ADMIN("ADMIN"),
    REGULAR("REGULAR");

    private String name;

    WalletRole(String name) {
        this.name = name;
    }
}
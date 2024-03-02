package com.example.virtualwallet.models.enums;

public enum WalletRole {

    ADMIN("ADMIN"),
    USER("USER");

    private String name;

    WalletRole(String name) {
        this.name = name;
    }
}
package com.example.virtualwallet.models.enums;

public enum WalletType {

    JOINT("JOINT"),
    MAIN("MAIN");

    private final String name;

    WalletType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
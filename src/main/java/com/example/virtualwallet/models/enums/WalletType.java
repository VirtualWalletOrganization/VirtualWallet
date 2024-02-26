package com.example.virtualwallet.models.enums;

public enum WalletType {

    JOINT("JOINT"),
    OVERDRAFT("OVERDRAFT"),
    REGULAR("REGULAR"),
    SAVINGS("SAVINGS");

    private String name;

    WalletType(String name) {
        this.name = name;
    }
}
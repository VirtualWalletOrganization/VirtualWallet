package com.example.virtualwallet.models.enums;

public enum Role {

    ADMIN(1),
    USER(2);

    private int value;

    Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
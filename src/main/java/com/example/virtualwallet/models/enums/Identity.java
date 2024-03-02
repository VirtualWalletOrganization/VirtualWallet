package com.example.virtualwallet.models.enums;

public enum Identity {

    APPROVED(1),
    INCOMPLETE(2),
    PENDING(3),
    REJECTED(4);

    private int value;

    Identity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
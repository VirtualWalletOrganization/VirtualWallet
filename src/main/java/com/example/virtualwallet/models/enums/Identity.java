package com.example.virtualwallet.models.enums;

public enum Identity {

    APPROVED("APPROVED"),
    INCOMPLETE("INCOMPLETE"),
    PENDING("PENDING"),
    REJECTED("REJECTED");

    private String name;

    Identity(String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }
}
package com.example.virtualwallet.models.enums;

public enum OverdraftType {

    STANDARD("STANDARD"),
    PREMIUM("PREMIUM"),
    PLATINUM("PLATINUM");

    private String name;

    OverdraftType(String name) {
        this.name = name;
    }
    }
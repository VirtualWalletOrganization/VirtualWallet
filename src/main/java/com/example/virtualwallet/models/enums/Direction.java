package com.example.virtualwallet.models.enums;

public enum Direction {

    INCOMING("INCOMING"),
    OUTGOING("OUTGOING");

    private String name;

    Direction(String name) {
        this.name = name;
    }
}
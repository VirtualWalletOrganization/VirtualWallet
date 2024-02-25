package com.example.virtualwallet.models;

public enum Identity {

    INCOMPLETE("INCOMPLETE"),
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private String name;

    Identity(String name) {
        this.name = name;
    }
}

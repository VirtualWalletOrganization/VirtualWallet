package com.example.virtualwallet.models.enums;

public enum TransferStatus {

    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    PENDING("PENDING");

    private String name;

    TransferStatus(String name) {
        this.name = name;
    }
}
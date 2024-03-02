package com.example.virtualwallet.models.enums;

public enum WalletType {

    JOINT(1),
    REGULAR(2);

    private final Integer id;

    WalletType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.WalletType;

public class WalletDto {

    private String currency;
    private WalletType walletType;

    public WalletDto() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }
}
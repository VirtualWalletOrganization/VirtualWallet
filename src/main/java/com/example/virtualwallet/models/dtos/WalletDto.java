package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.WalletType;
import jakarta.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class WalletDto {

    @NotNull(message = "Currency can't be empty.")
    @NotBlank(message = "Currency can't be blank.")
    private String currency;

    @NotNull(message = "Wallet type content can't be empty")
    private WalletType walletType;

    private Boolean isDefault;

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

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
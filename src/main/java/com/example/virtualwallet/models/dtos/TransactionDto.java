package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransactionDto {

    private int senderWalletId;
    private int receiver;
    @Positive(message = "Amount must be positive.")
    private BigDecimal amount;
    private String currency;
    private String description;

    public TransactionDto() {
    }

    public int getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(int senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public int getReceiver() {
        return receiver;
    }

    public void setReceiver(int receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
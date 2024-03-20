package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransferRequestDto {

    private int cardId;
    private int receiverWalletId;
//    @Positive(message = "Amount must be positive.")
    private BigDecimal amount;
    private String currency;
    private String description;

    public TransferRequestDto() {
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
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

    public int getReceiverWalletId() {
        return receiverWalletId;
    }

    public void setReceiverWalletId(int receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
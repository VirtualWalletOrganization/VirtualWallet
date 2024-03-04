package com.example.virtualwallet.models.dtos;

import java.math.BigDecimal;

public class TransactionDto {

    private String usernameReceiver;
    private BigDecimal amount;
    private String currency;
    private String description;

    public TransactionDto() {
    }

    public String getUsernameReceiver() {
        return usernameReceiver;
    }

    public void setUsernameReceiver(String usernameReceiver) {
        this.usernameReceiver = usernameReceiver;
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
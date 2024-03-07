package com.example.virtualwallet.models.dtos;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PresentableTransactionDto {

    private Timestamp dateTime;

    private BigDecimal transferAmount;

    private String description;

    public PresentableTransactionDto() {
    }

    public PresentableTransactionDto(Timestamp dateTime, BigDecimal transferAmount, String description) {
        this.dateTime = dateTime;
        this.transferAmount = transferAmount;
        this.description = description;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public String getDescription() {
        return description;
    }
}
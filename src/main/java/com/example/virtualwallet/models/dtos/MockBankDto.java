package com.example.virtualwallet.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class MockBankDto {

    @NotEmpty(message = "Card number cannot be empty.")
    @Size(min = 16, max = 16, message = "Card number should be 16 digits.")
    private String cardNumber;

    @NotEmpty(message = "Date cannot be empty.")
    private String expireDate;

    @NotEmpty(message = "CVV cannot be empty.")
    @Size(min = 3, max = 3, message = "CVV has to be 3 digits only.")
    private String cvv;

    @PositiveOrZero(message = "Transaction value cannot be empty or negative.")
    private BigDecimal value;

    private int walletId;

    public MockBankDto() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getWalletId() {
        return walletId;
    }

    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }
}
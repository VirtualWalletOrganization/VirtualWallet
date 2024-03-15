package com.example.virtualwallet.models.dtos;

import com.example.virtualwallet.models.enums.CardType;
import jakarta.validation.constraints.Pattern;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CardDto {

    @NotNull(message = "Card type content can't be empty")
    private String cardType;

    @Pattern(regexp = "\\d{16}",
            message = "Invalid card number")
    @NotNull(message = "Card number content can't be empty")
    private String cardNumber;

    // @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}")
    @Future(message = "Expiration date must be in the future")
    @NotNull(message = "Expiration date content can't be empty")
    private LocalDate expirationDate;

    @Pattern(regexp = ".{2,30}",
            message = "Invalid card holder name")
    @NotNull(message = "Card holder content can't be empty")
    private String cardHolder;

    @Pattern(regexp = "\\d{3}")
    @NotNull(message = "Check number content can't be empty")
    private String checkNumber;

    @Pattern(regexp = ".{3,}",
            message = "Invalid currency format")
    private String currency;

    public CardDto() {
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
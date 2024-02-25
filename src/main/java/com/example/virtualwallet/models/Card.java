package com.example.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Date;

public class Card {

    @Id
    @Column(name = "card_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType cardType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"id", "firstName", "lastName", "email", "role", "status", "isDeleted", "password", "profilePicture", "selfie", "photoCardId", "emailVerified", "overdraftEnabled", "overdraftLimit", "identity", "phoneNumber"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @JsonIgnoreProperties({"balance", "interestRate", "duration"})
    private Wallet wallet;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "check_number")
    private String checkNumber;
    @Column(name = "balance")
    private double balance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

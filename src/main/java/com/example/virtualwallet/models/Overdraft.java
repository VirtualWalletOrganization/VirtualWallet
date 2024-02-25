package com.example.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "overdrafts")
public class Overdraft {

    @Id
    @Column(name = "overdraft_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"id", "firstName", "lastName", "email", "role", "status", "isDeleted", "password", "profilePicture", "selfie", "photoCardId", "emailVerified", "overdraftEnabled", "overdraftLimit", "identity", "phoneNumber"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @JsonIgnoreProperties({"balance", "interestRate", "duration"})
    private Wallet wallet;

    @JsonIgnore
    @Column(name = "overdraft_enabled")
    private Boolean overdraftEnabled = Boolean.FALSE;

    @Column(name = "overdraft_limit")
    private double overdraftLimit;

    @Column(name = "interest_rate")
    private double interestRate;

    @Column(name = "last_charged_date")
    private int lastChargedDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Boolean getOverdraftEnabled() {
        return overdraftEnabled;
    }

    public void setOverdraftEnabled(Boolean overdraftEnabled) {
        this.overdraftEnabled = overdraftEnabled;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getLastChargedDate() {
        return lastChargedDate;
    }

    public void setLastChargedDate(int lastChargedDate) {
        this.lastChargedDate = lastChargedDate;
    }
}

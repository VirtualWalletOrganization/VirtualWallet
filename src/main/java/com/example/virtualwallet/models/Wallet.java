package com.example.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "saving_wallet")
public class Wallet {
    @Id
    @Column(name = "saving_wallet_id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"id", "firstName", "lastName", "email", "role", "status", "isDeleted", "password", "profilePicture", "selfie", "photoCardId", "emailVerified", "overdraftEnabled", "overdraftLimit", "identity", "phoneNumber"})
    private User user;
    @Column(name = "balance")
    private double balance;

    @Column(name = "interest_rate")
    private double interestRate;

    @Column(name = "duration")
    private int duration;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

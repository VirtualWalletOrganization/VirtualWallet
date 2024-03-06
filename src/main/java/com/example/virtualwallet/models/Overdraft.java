package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.OverdraftType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "overdrafts")
public class Overdraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "overdraft_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "overfraft_type_id")
    private OverdraftType overdraftType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "is_paid")
    private boolean isPaid;

    public Overdraft() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public OverdraftType getOverdraftType() {
        return overdraftType;
    }

    public void setOverdraftType(OverdraftType overdraftType) {
        this.overdraftType = overdraftType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
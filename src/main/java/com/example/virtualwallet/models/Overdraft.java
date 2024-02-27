package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.OverdraftType;
import jakarta.persistence.*;

import java.util.Date;

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
    private Date startDate;

    @Column(name = "due_date")
    private Date dueDate;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}
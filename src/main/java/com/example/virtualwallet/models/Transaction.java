package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Direction;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.TransactionType;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "transactions")
//@SecondaryTable(name = "recurring_transactions", pkJoinColumns = @PrimaryKeyJoinColumn(name = "transaction_id"))
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet walletSender;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet walletReceiver;

    @Column(name = "amount")
    private double amount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction")
    private Direction direction;

    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status_id")
    private Status status;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type_id")
    private TransactionType transactionType;

//    @Column(name = "recurring_transaction_id", table = "recurring_transactions")
//    private int recuringTransactionId;

    public Transaction() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Wallet getWalletSender() {
        return walletSender;
    }

    public void setWalletSender(Wallet walletSender) {
        this.walletSender = walletSender;
    }

    public Wallet getWalletReceiver() {
        return walletReceiver;
    }

    public void setWalletReceiver(Wallet walletReceiver) {
        this.walletReceiver = walletReceiver;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

//    public int getRecuringTransactionId() {
//        return recuringTransactionId;
//    }
//
//    public void setRecuringTransactionId(int recuringTransactionId) {
//        this.recuringTransactionId = recuringTransactionId;
//    }
}
package com.example.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
@Inheritance(strategy = InheritanceType.JOINED)
//@SecondaryTable(name = "recurring_transactions", pkJoinColumns = @PrimaryKeyJoinColumn(name = "transaction_id"))
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId;

    @ManyToOne
    @JoinColumn(name = "sender_wallet_id")
    private Wallet walletSender;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_sender_id")
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet walletReceiver;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_receiver_id")
    private User userReceiver;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "date")
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "transaction_status_id")
    private TransactionsStatus transactionsStatus;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionsType transactionsType;

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

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public Wallet getWalletReceiver() {
        return walletReceiver;
    }

    public void setWalletReceiver(Wallet walletReceiver) {
        this.walletReceiver = walletReceiver;
    }

    public User getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(User userReceiver) {
        this.userReceiver = userReceiver;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public TransactionsStatus getTransactionsStatus() {
        return transactionsStatus;
    }

    public void setTransactionsStatus(TransactionsStatus transactionsStatus) {
        this.transactionsStatus = transactionsStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionsType getTransactionsType() {
        return transactionsType;
    }

    public void setTransactionsType(TransactionsType transactionsType) {
        this.transactionsType = transactionsType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }
}
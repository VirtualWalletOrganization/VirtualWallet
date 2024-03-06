package com.example.virtualwallet.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
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

    @ManyToOne
    @JoinColumn(name = "receiver_wallet_id")
    private Wallet walletReceiver;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "direction")
//    private Direction direction;

    @Column(name = "date")
    private LocalDateTime date;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "transaction_status_id")
//    private Status status;

    @ManyToOne
    @JoinColumn(name = "transaction_status_id")
    private TransactionsStatus transactionsStatus;

    @Column(name = "description")
    private String description;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "transaction_type_id")
//    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionsType transactionsType;

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

//    public Direction getDirection() {
//        return direction;
//    }
//
//    public void setDirection(Direction direction) {
//        this.direction = direction;
//    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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

    //    public int getRecuringTransactionId() {
//        return recuringTransactionId;
//    }
//
//    public void setRecuringTransactionId(int recuringTransactionId) {
//        this.recuringTransactionId = recuringTransactionId;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId == that.transactionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
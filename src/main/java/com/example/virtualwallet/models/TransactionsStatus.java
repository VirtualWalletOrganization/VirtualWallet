package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions_statuses")
public class TransactionsStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_status_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_name")
    private Status transactionStatus;

    public TransactionsStatus() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Status transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
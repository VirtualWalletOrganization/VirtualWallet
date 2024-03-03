package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.TransactionType;
import jakarta.persistence.*;

@Entity
@Table(name = "transactions_types")
public class TransactionsType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_type_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type_name")
    private TransactionType transactionType;

    public TransactionsType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}

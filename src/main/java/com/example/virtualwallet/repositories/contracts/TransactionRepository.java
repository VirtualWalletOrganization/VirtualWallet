package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Optional<List<Transaction>> getAllTransaction();

    Optional<Transaction> getTransactionById(int transactionId);

    Optional<List<Transaction>> getAllTransactionByUserId(int userId);

    Transaction create(Transaction transaction);

    void update(Transaction transaction);

    void delete(Transaction transaction);
}

package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransfers();

    Transaction getTransactionById(int transactionId);

    void createTransaction(int senderUserId, int recipientUserId, int walletId, double amount);

    void confirmTransaction(int transactionId, int senderUserId, int recipientId, double amount);

    void updateTransaction(Transaction transaction);

    void deleteTransaction(Transaction transaction);
}

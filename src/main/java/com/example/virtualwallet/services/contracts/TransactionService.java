package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(int transactionId);

    void createTransaction(Transaction transaction, Wallet walletSender, User sender);

    void updateTransaction(Transaction transaction);

    void delete(Transaction transaction);
}
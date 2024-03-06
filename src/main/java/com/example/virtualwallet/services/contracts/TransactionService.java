package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Status;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(int transactionId);

    List<Transaction> getTransactionsByStatus(Status status);

    void confirmTransaction(Transaction transaction, Wallet walletSender, User sender);

    void createTransaction(Transaction transaction, Wallet walletSender, User sender);

    void updateTransaction(Transaction transaction, Wallet walletSender, User sender);

    void delete(Transaction transaction, User sender);

    void requestMoney(Transaction transaction, Wallet walletSender, User sender);
}
package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import com.example.virtualwallet.utils.TransactionHistoryFilterOptions;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    List<Transaction> getAllTransactions(TransactionFilterOptions transactionFilterOptions);

    Transaction getTransactionById(int transactionId);

    List<Transaction> getAllTransactionsByUserId(int userId, TransactionHistoryFilterOptions transactionHistoryFilterOptions);

    Optional<List<Transaction>> getAllTransactionsByStatus(User user);

    Optional<List<Transaction>> getAllTransactionsByTransactionType(User user);

    List<Transaction> getAllTransactionsByWalletId(Wallet wallet);

    void confirmTransaction(Transaction transaction, Wallet walletSender, User sender);

    Transaction createTransaction(Transaction transaction, Wallet walletSender, User userSender,
                                  Wallet walletReceiver, User userReceiver);

    void updateTransaction(Transaction transaction, User userSender);

    void delete(Transaction transaction, User sender);

    void createRecurringTransaction(Transaction transaction);

    Transaction requestMoney(Transaction transaction, Wallet walletReceiver, User userReceiver);
}
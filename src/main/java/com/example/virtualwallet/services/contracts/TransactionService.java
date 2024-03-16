package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {

    List<Transaction> getAllTransactions(TransactionFilterOptions transactionFilterOptions);

    Transaction getTransactionById(int transactionId);

    List<Transaction> getAllTransactionsByUserId(int userId);

    List<Transaction> getAllTransactionsByStatus(Status status);

    List<Transaction> getAllTransactionsByWalletId(Wallet wallet);

    void confirmTransaction(Transaction transaction, Wallet walletSender, User sender);

    Transaction createTransaction(Transaction transaction, Wallet walletSender, User userSender,
                                  Wallet walletReceiver, User userReceiver);

    void updateTransaction(Transaction transaction, User userSender);

    void delete(Transaction transaction, User sender);

    void createRecurringTransaction(Transaction transaction);

    Transaction requestMoney(Transaction transaction, Wallet walletReceiver, User userReceiver);
}
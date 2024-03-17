package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Optional<List<Transaction>> getAllTransactions(TransactionFilterOptions transactionFilterOptions);

    Optional<Transaction> getTransactionById(int transactionId);

    Optional <List<Transaction>> getAllTransactionsByUserId(int userId);

    Optional<List<Transaction>> getAllTransactionsByWalletId(int walletId);

    Optional<List<Transaction>> getAllTransactionsByStatus(Status status);

    Transaction create(Transaction transaction);

    void update(Transaction transaction);

    void delete(Transaction transaction);
}
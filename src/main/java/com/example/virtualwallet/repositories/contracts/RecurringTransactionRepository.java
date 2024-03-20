package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;

import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository {

    Optional<List<RecurringTransaction>> getAllRecurringTransactions();

    Optional<RecurringTransaction> getRecurringTransactionById(int transactionId);

    Optional<List<RecurringTransaction>> getRecurringTransactionByUserId(int userId);

    RecurringTransaction create(RecurringTransaction recurringTransaction);

    void update(RecurringTransaction recurringTransaction);

    void delete(RecurringTransaction recurringTransaction);
}
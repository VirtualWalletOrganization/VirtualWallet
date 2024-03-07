package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.Transaction;

import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository {

    Optional<List<RecurringTransaction>> getAllRecurringTransactions();

    Optional<RecurringTransaction> getRecurringTransactionById(int transactionId);

    RecurringTransaction create(RecurringTransaction recurringTransaction);

    void update(RecurringTransaction recurringTransaction);

    void delete(RecurringTransaction recurringTransaction);
}
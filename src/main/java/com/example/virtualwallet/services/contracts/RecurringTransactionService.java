package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.util.List;
import java.util.Optional;

public interface RecurringTransactionService {

    List<RecurringTransaction>getAllRecurringTransactions();
    RecurringTransaction getRecurringTransactionById(int transactionId);

   Optional<List< RecurringTransaction>> getRecurringTransactionByUserId(int userId);

    void createRecurringTransaction(RecurringTransaction recurringTransaction,
                                    Wallet walletSender, User user, Wallet walletReceiver, User userReceiver);

    void updateRecurringTransaction(RecurringTransaction recurringTransaction, User user);

    void cancelRecurringTransaction(RecurringTransaction recurringTransaction, User user);

    void executeRecurringTransaction();
}
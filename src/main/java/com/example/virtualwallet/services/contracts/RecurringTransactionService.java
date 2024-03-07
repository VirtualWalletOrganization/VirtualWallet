package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.util.List;

public interface RecurringTransactionService  {
    List<RecurringTransaction> getAllRecurringTransactions();
    void createRecurringTransaction(RecurringTransaction recurringTransaction,
                                   Wallet walletSender,User user,Wallet walletReceiver,User userReceiver);

    void executeRecurringTransaction();
}

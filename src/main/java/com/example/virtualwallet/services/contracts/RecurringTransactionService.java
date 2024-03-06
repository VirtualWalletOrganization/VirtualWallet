package com.example.virtualwallet.services.contracts;


import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

public interface RecurringTransactionService  {
    void createRecurringTransaction(RecurringTransaction recurringTransaction, Wallet walletSender, User user);

}

package com.example.virtualwallet.services;

import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.repositories.contracts.RecurringTransactionRepository;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionService transactionService;

    @Autowired
    public RecurringTransactionServiceImpl(RecurringTransactionRepository recurringTransactionRepository, TransactionService transactionService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionService = transactionService;
    }


    public void createRecurringTransaction(RecurringTransaction recurringTransaction, Wallet walletSender, User user) {
        recurringTransactionRepository.create(recurringTransaction);
        transactionService.createTransaction(recurringTransaction,walletSender,user);
    }

//    private Date parseDate(String dateString) {
//        // Implement date parsing logic here
//    }
}

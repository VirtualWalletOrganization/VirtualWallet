package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.RecurringTransactionDto;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.models.enums.Frequency;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public Transaction fromDtoMoney(TransactionDto transactionDto,
                                    Wallet walletSender,
                                    User userSender,
                                    Wallet walletReceiver,
                                    User userReceiver) {
        Transaction transaction = new Transaction();
        transaction.setWalletSender(walletSender);
        transaction.setUserSender(userSender);
        transaction.setWalletReceiver(walletReceiver);
        transaction.setUserReceiver(userReceiver);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency("USD");
        transaction.setDate(Timestamp.valueOf(LocalDateTime.now()));

        TransactionsStatus transactionsStatus = new TransactionsStatus();
        transactionsStatus.setId(Status.PENDING.ordinal() + 1);
        transactionsStatus.setTransactionStatus(Status.PENDING);
        transaction.setTransactionsStatus(transactionsStatus);

        transaction.setDescription(transactionDto.getDescription());

        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setId(TransactionType.SINGLE.ordinal() + 1);
        transactionsType.setTransactionType(TransactionType.SINGLE);
        transaction.setTransactionsType(transactionsType);

        return transaction;
    }

    public RecurringTransaction fromDtoTransaction(RecurringTransactionDto recurringTransactionDto, Wallet senderWallet,
                                                   User userSender, Wallet walletReceiver, User userReceiver) {
        RecurringTransaction recurringTransaction = new RecurringTransaction();
        Transaction transaction = fromDtoMoney(recurringTransactionDto, senderWallet, userSender,
                walletReceiver, userReceiver);

        recurringTransaction.setWalletSender(transaction.getWalletSender());
        recurringTransaction.setUserSender(userSender);
        recurringTransaction.setWalletReceiver(transaction.getWalletReceiver());
        recurringTransaction.setUserReceiver(userReceiver);
        recurringTransaction.setAmount(transaction.getAmount());
        recurringTransaction.setCurrency(transaction.getCurrency());
        recurringTransaction.setDate(Timestamp.valueOf(LocalDateTime.now()));

        recurringTransaction.setTransactionsStatus(transaction.getTransactionsStatus());
        recurringTransaction.getTransactionsStatus().setId(Status.PENDING_RECURRING_REQUEST.ordinal() + 1);
        recurringTransaction.getTransactionsStatus().setTransactionStatus(Status.PENDING_RECURRING_REQUEST);
        recurringTransaction.setTransactionsType(transaction.getTransactionsType());
        recurringTransaction.getTransactionsType().setId(TransactionType.RECURRING.ordinal() + 1);
        recurringTransaction.getTransactionsType().setTransactionType(TransactionType.RECURRING);

        recurringTransaction.setDescription(recurringTransactionDto.getDescription());
        recurringTransaction.setFrequency(Frequency.valueOf(recurringTransactionDto.getFrequency()));
        recurringTransaction.setStartDate(recurringTransactionDto.getStartDate());
        recurringTransaction.setEndDate(recurringTransactionDto.getEndDate());

        return recurringTransaction;
    }

    public RecurringTransaction fromDtoTransactionUpdate(RecurringTransactionDto recurringTransactionDto,
                                                         RecurringTransaction recurringTransaction) {
        recurringTransaction.setFrequency(Frequency.valueOf(recurringTransactionDto.getFrequency()));
        recurringTransaction.setStartDate(recurringTransactionDto.getStartDate());
        recurringTransaction.setEndDate(recurringTransactionDto.getEndDate());
        return recurringTransaction;
    }

    public Transaction fromDtoRecurring(Transaction recurringTransaction) {
        Transaction transaction = new Transaction();
        transaction.setWalletSender(recurringTransaction.getWalletSender());
        transaction.setUserSender(recurringTransaction.getUserSender());
        transaction.setWalletReceiver(recurringTransaction.getWalletReceiver());
        transaction.setUserReceiver(recurringTransaction.getUserReceiver());
        transaction.setAmount(recurringTransaction.getAmount());
        transaction.setCurrency(recurringTransaction.getCurrency());
        transaction.setDate(Timestamp.valueOf(LocalDateTime.now()));
        transaction.setTransactionsStatus(recurringTransaction.getTransactionsStatus());

        recurringTransaction.getTransactionsStatus().setId(Status.PENDING_RECURRING_REQUEST.ordinal());
        recurringTransaction.getTransactionsStatus().setTransactionStatus(Status.PENDING_RECURRING_REQUEST);

        transaction.setDescription(recurringTransaction.getDescription());
        transaction.setTransactionsType(recurringTransaction.getTransactionsType());


        return transaction;
    }
}
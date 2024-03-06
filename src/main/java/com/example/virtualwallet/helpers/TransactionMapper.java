package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.RecurringTransactionDto;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.models.enums.Interval;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.TransactionType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class TransactionMapper {

    public Transaction fromDtoMoney(TransactionDto transactionDto,
                                    Wallet walletSender,
                                    User userSender,
                                    Wallet walletReceiver,
                                    User userReceiver) {
        Transaction transaction = new Transaction();
        transaction.setWalletSender(walletSender);
        transaction.setWalletReceiver(walletReceiver);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        // transaction.setDirection(Direction.OUTGOING);
        transaction.setDate(LocalDateTime.now());
        TransactionsStatus transactionsStatus = new TransactionsStatus();
        transactionsStatus.setId(Status.PENDING.ordinal());
        transactionsStatus.setTransactionStatus(Status.PENDING);
        transaction.setTransactionsStatus(transactionsStatus);
        transaction.setDescription("Transaction from " + userSender.getUsername() + " to " + userReceiver.getUsername());

        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setId(TransactionType.SINGLE.ordinal());
        transactionsType.setTransactionType(TransactionType.SINGLE);
        transaction.setTransactionsType(transactionsType);

        return transaction;
    }

    public RecurringTransaction fromDto(RecurringTransactionDto recurringTransactionDto, Wallet senderWallet,
                                        User userSender, Wallet walletReceiver, User userReceiver) {
        RecurringTransaction recurringTransaction = new RecurringTransaction();

        Transaction transaction = fromDtoMoney(recurringTransactionDto, senderWallet, userSender,
                walletReceiver, userReceiver);
        recurringTransaction.setWalletSender(transaction.getWalletSender());
        recurringTransaction.setWalletReceiver(transaction.getWalletReceiver());
        recurringTransaction.setAmount(transaction.getAmount());
        recurringTransaction.setCurrency(transaction.getCurrency());
        // recurringTransaction.setDirection(transaction.getDirection());
        recurringTransaction.setDate(transaction.getDate());
        recurringTransaction.setDescription(transaction.getDescription());
        recurringTransaction.setTransactionsStatus(transaction.getTransactionsStatus());
        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setTransactionType(TransactionType.RECURRING);
        recurringTransaction.setTransactionsType(transactionsType);
        recurringTransaction.setIntervals(Interval.valueOf(recurringTransactionDto.getInterval()));

//        Instant startInstant = recurringTransactionDto.getStartDate().toInstant();
//        LocalDateTime startDateTime = LocalDateTime.ofInstant(startInstant, ZoneOffset.UTC);
//        recurringTransaction.setStartDate(startDateTime);
//        Instant endInstant = recurringTransactionDto.getEndDate().toInstant();
//        LocalDateTime endDateTime = LocalDateTime.ofInstant(endInstant, ZoneOffset.UTC);
//        recurringTransaction.setEndDate(endDateTime);
        recurringTransaction.setStartDate(recurringTransactionDto.getStartDate());
        recurringTransaction.setEndDate(recurringTransactionDto.getEndDate());
        return recurringTransaction;
    }
}
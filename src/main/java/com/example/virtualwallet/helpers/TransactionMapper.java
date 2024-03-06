package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.RecurringTransactionDto;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.models.enums.Direction;
import com.example.virtualwallet.models.enums.Interval;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.TransactionType;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class TransactionMapper {

    private final UserService userService;

    public TransactionMapper(UserService userService) {
        this.userService = userService;
    }

    public Transaction fromDtoMoney(Wallet walletSender, TransactionDto transactionDto, User userSender) {
        Transaction transaction = new Transaction();
        transaction.setWalletSender(walletSender);

        User userReceiver = userService.getByUsername(transactionDto.getReceiver());
        Wallet walletReceiver = userReceiver.getCreatedWallets().stream()
                .filter(Wallet::getDefault)
                .findFirst()
                .get();
        transaction.setWalletReceiver(walletReceiver);

        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setDirection(Direction.OUTGOING);
        transaction.setDate(new Date());

        BigDecimal newBalance = walletSender.getBalance().subtract(transaction.getAmount());
        if (walletSender.getBalance().compareTo(newBalance) < 0) {
            // Condition logic
//        } if (walletSender.getBalance().compareTo(walletSender.getBalance().subtract(transaction.getAmount())) < 0) {
            TransactionsStatus transactionsStatus = new TransactionsStatus();
            transactionsStatus.setId(Status.FAILED.ordinal());
            transactionsStatus.setTransactionStatus(Status.FAILED);
        } else {
            TransactionsStatus transactionsStatus = new TransactionsStatus();
            transactionsStatus.setId(Status.COMPLETED.ordinal());
            transactionsStatus.setTransactionStatus(Status.COMPLETED);
            transaction.setTransactionsStatus(transactionsStatus);
        }

        transaction.setDescription("Transaction from " + userSender.getUsername() + " to " + userReceiver.getUsername());

        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setId(TransactionType.SINGLE.ordinal());
        transactionsType.setTransactionType(TransactionType.SINGLE);
        transaction.setTransactionsType(transactionsType);

        return transaction;
    }

    public Transaction fromDto(int transactionId, Wallet senderWallet, TransactionDto transactionDto, User userSender) {
        Transaction transaction = fromDtoMoney(senderWallet, transactionDto, userSender);
        transaction.setTransactionId(transactionId);
        TransactionsStatus transactionsStatus = new TransactionsStatus();
        transactionsStatus.setId(Status.PENDING.ordinal());
        transactionsStatus.setTransactionStatus(Status.PENDING);
        transaction.setTransactionsStatus(transactionsStatus);

        return transaction;
    }

    public RecurringTransaction fromDto(Wallet senderWallet, RecurringTransactionDto recurringTransactionDto,
                                        User userSender) {
        RecurringTransaction recurringTransaction = new RecurringTransaction();

        Transaction transaction = fromDtoMoney(senderWallet, recurringTransactionDto, userSender);
        recurringTransaction.setWalletSender(transaction.getWalletSender());
        recurringTransaction.setWalletReceiver(transaction.getWalletReceiver());
        recurringTransaction.setAmount(transaction.getAmount());
        recurringTransaction.setCurrency(transaction.getCurrency());
        recurringTransaction.setDirection(transaction.getDirection());
        recurringTransaction.setDate(transaction.getDate());
        recurringTransaction.setDescription(transaction.getDescription());
        recurringTransaction.setTransactionsStatus(transaction.getTransactionsStatus());
        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setTransactionType(TransactionType.RECURRING);
        recurringTransaction.setTransactionsType(transactionsType);
        recurringTransaction.setIntervals(Interval.valueOf(recurringTransactionDto.getInterval()));
        recurringTransaction.setStartDate(recurringTransactionDto.getStartDate());
        recurringTransaction.setEndDate(recurringTransactionDto.getEndDate());
        return recurringTransaction;
    }
}
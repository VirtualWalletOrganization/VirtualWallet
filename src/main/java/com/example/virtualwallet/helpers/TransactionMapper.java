package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.models.dtos.TransferRequestDto;
import com.example.virtualwallet.models.enums.Direction;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.TransactionType;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TransactionMapper {

    private final UserService userService;

    public TransactionMapper(UserService userService) {
        this.userService = userService;
    }

    public Transaction fromDtoMoneyOut(Wallet senderWallet, TransactionDto transactionDto, User userSender) {
        Transaction transaction = new Transaction();
        transaction.setWalletSender(senderWallet);

        User userReceiver = userService.getByUsername(transactionDto.getUsernameReceiver());
        transaction.getUsernameReceiverId().setUsername(userReceiver.getUsername());

        transaction.setAmount(transactionDto.getAmount());
        transaction.setCurrency(transactionDto.getCurrency());
        transaction.setDirection(Direction.OUTGOING);
        transaction.setDate(new Date());

        TransactionsStatus transactionsStatus = new TransactionsStatus();
        transactionsStatus.setTransactionStatus(Status.PENDING);
        transaction.setTransactionsStatus(transactionsStatus);
        transaction.setDescription("Transaction from " + userSender.getUsername() + " to " + userReceiver.getUsername());

        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setTransactionType(TransactionType.SINGLE);
        transaction.setTransactionsType(transactionsType);
        return transaction;
    }

    public Transaction fromDtoMoneyIn(Wallet receiverWallet, TransactionDto transactionDto, User userReceiver) {
        Transaction transaction = fromDtoMoneyOut(receiverWallet, transactionDto, userReceiver);
        transaction.setDirection(Direction.INCOMING);
        return transaction;
    }
}
package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.TransactionsStatus;
import com.example.virtualwallet.models.TransactionsType;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.TransactionDto;
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

    public Transaction toDto(TransactionDto dto, int senderUserId, int recipientUserId) {
        User sender = userService.getById(senderUserId);
        User recipient = userService.getById(recipientUserId);

        Transaction transaction = new Transaction();
//        transaction.setWalletSender(dto.getSenderWallet());
//        transaction.setWalletReceiver(dto.getReceiverWallet());
        transaction.setCurrency(dto.getCurrency());
        transaction.setDirection(Direction.OUTGOING);
        transaction.setDate(new Date());

        TransactionsStatus transactionsStatus = new TransactionsStatus();
        transactionsStatus.setTransactionStatus(Status.PENDING);
        transaction.setTransactionsStatus(transactionsStatus);
        transaction.setDescription("Transaction from " + sender.getUsername() + " to " + recipient.getUsername());

        TransactionsType transactionsType = new TransactionsType();
        transactionsType.setTransactionType(TransactionType.SINGLE);
        transaction.setTransactionsType(transactionsType);

        return transaction;
    }
}
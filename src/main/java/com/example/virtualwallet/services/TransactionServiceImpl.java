package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.virtualwallet.utils.CheckPermissions.checkAccessPermissionsUser;
import static com.example.virtualwallet.utils.CheckPermissions.checkBlockOrDeleteUser;
import static com.example.virtualwallet.utils.Messages.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, UserService userService, WalletService walletService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.walletService = walletService;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.getAllTransactions()
                .orElseThrow(() -> new EntityNotFoundException("Transactions"));
    }

    @Override
    public Transaction getTransactionById(int transactionId) {
        return transactionRepository.getTransactionById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "id", String.valueOf(transactionId)));
    }

    @Override
    public void createTransaction(Transaction transaction, Wallet walletSender, User sender) {
        User recipient = userService.getByUsername(transaction.getUsernameReceiverId().getUsername());
        Wallet walletRecipient = walletService.getDefaultWallet(recipient.getId());

        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkAccessPermissionsUser(recipient.getId(), sender, ERROR_TRANSACTION);

        if (walletSender.getBalance().compareTo(walletSender.getBalance().subtract(transaction.getAmount())) < 0) {
            transaction.getTransactionsStatus().setTransactionStatus(Status.FAILED);
            transactionRepository.update(transaction);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
            //TODO implement check for overdraft
        }

        transaction.getTransactionsStatus().setTransactionStatus(Status.COMPLETED);
        transactionRepository.update(transaction);
        walletSender.setBalance(walletSender.getBalance().subtract(transaction.getAmount()));
        walletRecipient.setBalance(walletSender.getBalance().add(transaction.getAmount()));
        walletService.update(walletSender, sender);
        walletService.update(walletRecipient, recipient);
    }

    @Override
    public void updateTransaction(Transaction transaction, User sender) {
        User recipient = userService.getByUsername(transaction.getUsernameReceiverId().getUsername());
        checkAccessPermissionsUser(recipient.getId(), sender, ERROR_TRANSACTION);
        walletService.getDefaultWallet(recipient.getId());
        transactionRepository.create(transaction);
    }

    public void delete(Transaction transaction, User sender) {
        checkAccessPermissionsUser(transaction.getUsernameReceiverId().getId(), sender, ERROR_TRANSACTION);
        transactionRepository.delete(transaction);
    }
}
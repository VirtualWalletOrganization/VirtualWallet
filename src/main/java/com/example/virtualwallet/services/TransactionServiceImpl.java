package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.checkBlockOrDeleteUser;
import static com.example.virtualwallet.utils.CheckPermissions.checkPermissionExistingUsersInWallet;
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
    public List<Transaction> getAllTransactionsByStatus(Status status, int walletId) {
        return transactionRepository.getAllTransactionsByStatus(status,walletId)
                .orElseThrow(() -> new EntityNotFoundException("Transactions", "status", String.valueOf(status)));
    }

    @Override
    public void confirmTransaction(Transaction transaction, Wallet walletSender, User sender) {
        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);
    }

    @Override
    public void createTransaction(Transaction transaction, Wallet walletSender, User userSender,
                                  Wallet walletReceiver, User userReceiver) {
        checkBlockOrDeleteUser(userSender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, userSender, ERROR_TRANSACTION);

        if (!isValidRequestTransferMoney(transaction, walletSender)) {
            transaction.getTransactionsStatus().setId(Status.FAILED.ordinal());
            transaction.getTransactionsStatus().setTransactionStatus(Status.FAILED);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
            //TODO implement check for overdraft
        } else {
            transaction.getTransactionsStatus().setId(Status.COMPLETED.ordinal());
            transaction.getTransactionsStatus().setTransactionStatus(Status.COMPLETED);
        }
        transactionRepository.create(transaction);

        walletSender.setBalance(walletSender.getBalance().subtract(transaction.getAmount()));
        walletReceiver.setBalance(walletSender.getBalance().add(transaction.getAmount()));
        walletService.update(walletSender, userSender);
        walletService.update(transaction.getWalletReceiver(), userReceiver);
    }

    @Override
    public void updateTransaction(Transaction transaction,User userSender) {
        checkBlockOrDeleteUser(userSender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(transaction.getWalletSender(), userSender, ERROR_TRANSACTION);
        if (transaction.getTransactionsStatus().getTransactionStatus() == (Status.PENDING)) {
            if (!isValidRequestTransferMoney(transaction, transaction.getWalletSender())) {
                transaction.getTransactionsStatus().setTransactionStatus(Status.FAILED);
                throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
                //TODO implement check for overdraft
            } else {
                transaction.getTransactionsStatus().setId(Status.COMPLETED.ordinal());
                transaction.getTransactionsStatus().setTransactionStatus(Status.COMPLETED);
            }
            transactionRepository.update(transaction);
        } else {
            throw new EntityNotFoundException("Request transaction");

        }
        List<User> users=walletService.getAllUsersByWalletId(transaction.getWalletReceiver().getId());

        transaction.getWalletSender().setBalance(transaction.getWalletSender().getBalance().subtract(transaction.getAmount()));
            transaction.getWalletReceiver().setBalance(transaction.getWalletReceiver().getBalance().add(transaction.getAmount()));
            walletService.update(transaction.getWalletSender(), userSender);
           // walletService.update(transaction.getWalletReceiver());
        }

        public void delete (Transaction transaction, User sender){
            checkPermissionExistingUsersInWallet(transaction.getWalletSender(), sender, ERROR_TRANSACTION);
            transactionRepository.delete(transaction);
        }

        public void requestMoney (Transaction transaction, Wallet walletSender, User sender){
            checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
            checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);
            transactionRepository.create(transaction);
        }

        private boolean isValidRequestTransferMoney (Transaction transaction, Wallet walletSender){

            BigDecimal balanceAfterTransfer = walletSender.getBalance().subtract(transaction.getAmount());
            return balanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;
        }
    }
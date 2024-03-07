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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
    public Page<Transaction> getAll(Pageable pageable) {
        return transactionRepository.getAll(pageable);
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
    public List<Transaction> getAllTransactionsByStatus(Status status) {
        return transactionRepository.getAllTransactionsByStatus(status)
                .orElseThrow(() -> new EntityNotFoundException("Transactions", "status", String.valueOf(status)));
    }

    @Override
    public void confirmTransaction(Transaction transaction, Wallet walletSender, User sender) {
        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);
    }

    @Override
    public Transaction createTransaction(Transaction transaction, Wallet walletSender, User userSender,
                                         Wallet walletReceiver, User userReceiver) {

        checkBlockOrDeleteUser(userSender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, userSender, ERROR_TRANSACTION);
        //TODO implement check for overdraft
        isValidRequestTransferMoney(transaction);
        transactionRepository.create(transaction);

        walletSender.setBalance(walletSender.getBalance().subtract(transaction.getAmount()));
        walletSender.getSentTransactions().add(transaction);
        walletReceiver.setBalance(walletReceiver.getBalance().add(transaction.getAmount()));
        walletReceiver.getReceiverTransactions().add(transaction);
        walletService.update(walletSender, userSender);
        walletService.update(transaction.getWalletReceiver(), userReceiver);
        return transaction;
    }

    @Override
    public void updateTransaction(Transaction transaction, User userSender) {
        checkBlockOrDeleteUser(userSender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(transaction.getWalletSender(), userSender, ERROR_TRANSACTION);
        //TODO implement check for overdraft

        if (transaction.getTransactionsStatus().getTransactionStatus() == (Status.PENDING)) {
            isValidRequestTransferMoney(transaction);
            transactionRepository.update(transaction);
        } else {
            throw new EntityNotFoundException("Request transaction");
        }

        transaction.getWalletSender().setBalance(transaction.getWalletSender().getBalance().subtract(transaction.getAmount()));
        transaction.getWalletSender().getSentTransactions().add(transaction);
        walletService.update(transaction.getWalletSender(), userSender);

        List<User> users = walletService.getAllUsersByWalletId(transaction.getWalletReceiver().getId());
        users.forEach(user -> user.getWallets().stream()
                .filter(wallet -> wallet.getId() == transaction.getWalletReceiver().getId())
                .findFirst()
                .ifPresent(wallet -> {
                    wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                    wallet.getReceiverTransactions().add(transaction);
                    walletService.update(wallet, user);

                }));
    }

    public void delete(Transaction transaction, User sender) {
        checkPermissionExistingUsersInWallet(transaction.getWalletSender(), sender, ERROR_TRANSACTION);
        transactionRepository.delete(transaction);
    }

    public void createRecurringTransaction(Transaction transaction) {
        transactionRepository.create(transaction);
    }

    public Transaction requestMoney(Transaction transaction, Wallet walletReceiver, User userReceiver) {
        checkBlockOrDeleteUser(userReceiver, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletReceiver, userReceiver, ERROR_TRANSACTION);

        userReceiver.getWallets().stream()
                .filter(wallet -> wallet.getId() == walletReceiver.getId()
                        && wallet.getDefault())
                .findFirst()
                .ifPresent(wallet -> {
                    wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                    wallet.getReceiverTransactions().add(transaction);
                    walletService.update(wallet, userReceiver);
                });
        return transactionRepository.
                create(transaction);
    }

    private void isValidRequestTransferMoney(Transaction transaction) {
        if (!isValidRequestEnoughMoney(transaction, transaction.getWalletSender())) {
            transaction.getTransactionsStatus().setId(Status.FAILED.ordinal());
            transaction.getTransactionsStatus().setTransactionStatus(Status.FAILED);
            transactionRepository.update(transaction);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
        } else {
            transaction.getTransactionsStatus().setId(Status.COMPLETED.ordinal());
            transaction.getTransactionsStatus().setTransactionStatus(Status.COMPLETED);
        }
    }

    private boolean isValidRequestEnoughMoney(Transaction transaction, Wallet walletSender) {
        BigDecimal balanceAfterTransfer = walletSender.getBalance().subtract(transaction.getAmount());
        return balanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;
    }

    public List<Transaction> getAllTransactionsByWalletId(Wallet wallet) {
//        Set<Transaction> sentTransactions = wallet.getSentTransactions();
//        Set<Transaction> receivedTransactions = wallet.getSentTransactions();
//        Set<Transaction> allTransactions = new HashSet<>();
//        allTransactions.addAll(sentTransactions);
//        allTransactions.addAll(receivedTransactions);
//
//        List<Transaction> sortedTransactions = allTransactions.stream()
//                .sorted(Comparator.comparing(Transaction::getDate))
//                .toList();
//        return allTransactions;
        return transactionRepository.getAllTransactionsByWalletId(wallet.getId())
                .orElseThrow(() -> new EntityNotFoundException("Transactions", "wallet id", String.valueOf(wallet.getId())));
    }
}
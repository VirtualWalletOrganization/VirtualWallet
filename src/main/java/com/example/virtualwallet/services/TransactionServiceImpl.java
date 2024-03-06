package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.TransactionsStatus;
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
    public List<Transaction> getTransactionsByStatus(Status status) {
        return transactionRepository.getTransactionsByStatus(status)
                .orElseThrow(() -> new EntityNotFoundException("Transactions", "status", String.valueOf(status)));
    }
    @Override
    public void confirmTransaction(Transaction transaction, Wallet walletSender, User sender) {
        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);
    }

    @Override
    public void createTransaction(Transaction transaction, Wallet walletSender, User sender) {
        //we already check this in the mapper
//        User recipient = userService.getByUsername(transaction.getReceiver().getUsername());
//        Wallet walletRecipient = walletService.getDefaultWallet(recipient.getId());

        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);

        if (transaction.getTransactionsStatus().equals(Status.FAILED)) {
            transactionRepository.update(transaction);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
            //TODO implement check for overdraft
        }
        walletSender.setBalance(walletSender.getBalance().subtract(transaction.getAmount()));
        transaction.getWalletReceiver().setBalance(transaction.getWalletReceiver().getBalance().add(transaction.getAmount()));
        transactionRepository.create(transaction);
        walletService.update(walletSender, sender);
        walletService.update(transaction.getWalletReceiver(), transaction.getWalletReceiver().getCreator());
    }

    @Override
    public void updateTransaction(Transaction transaction, Wallet walletSender, User sender) {
//        User recipient = userService.getByUsername(transaction.getReceiver().getUsername());
//        Wallet walletRecipient = walletService.getDefaultWallet(recipient.getId());

        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);

//        if (walletSender.getBalance().compareTo(walletSender.getBalance().subtract(transaction.getAmount())) < 0) {
//            TransactionsStatus transactionsStatus = new TransactionsStatus();
//            transactionsStatus.setId(Status.FAILED.ordinal());
//            transactionsStatus.setTransactionStatus(Status.FAILED);
//            transactionRepository.update(transaction);
//            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
//            //TODO implement check for overdraft
//        }

        if (transaction.getTransactionsStatus().equals(Status.FAILED)) {
            transactionRepository.update(transaction);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
            //TODO implement check for overdraft
        }

        TransactionsStatus transactionsStatus = new TransactionsStatus();
        transactionsStatus.setId(Status.COMPLETED.ordinal());
        transactionsStatus.setTransactionStatus(Status.COMPLETED);
        transaction.setTransactionsStatus(transactionsStatus);
        transactionRepository.update(transaction);

        walletSender.setBalance(walletSender.getBalance().subtract(transaction.getAmount()));
        transaction.getWalletReceiver().setBalance(transaction.getWalletReceiver().getBalance().add(transaction.getAmount()));
        walletService.update(walletSender, sender);
        //it will always be the creator of the wallet because we will always transfer to default wallet
        walletService.update(transaction.getWalletReceiver(), transaction.getWalletReceiver().getCreator());
    }

    public void delete(Transaction transaction, User sender) {
        checkPermissionExistingUsersInWallet(transaction.getWalletSender(), sender, ERROR_TRANSACTION);
        transactionRepository.delete(transaction);
    }

    public void requestMoney(Transaction transaction, Wallet walletSender, User sender) {
        checkBlockOrDeleteUser(sender, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(walletSender, sender, ERROR_TRANSACTION);
        transactionRepository.create(transaction);
    }
}
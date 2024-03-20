package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.TransactionMapper;
import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Frequency;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.RecurringTransactionRepository;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.checkBlockOrDeleteUser;
import static com.example.virtualwallet.utils.CheckPermissions.checkPermissionExistingUsersInWallet;
import static com.example.virtualwallet.utils.Messages.*;

@Service
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public RecurringTransactionServiceImpl(RecurringTransactionRepository recurringTransactionRepository,
                                           TransactionService transactionService,
                                           WalletService walletService, TransactionMapper transactionMapper) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<RecurringTransaction> getAllRecurringTransactions() {
        return recurringTransactionRepository.getAllRecurringTransactions()
                .orElseThrow(() -> new EntityNotFoundException("Recurring Transactions"));
    }

    @Override
    public RecurringTransaction getRecurringTransactionById(int transactionId) {
        return recurringTransactionRepository.getRecurringTransactionById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Recurring Transactions"));
    }

    @Override
    public Optional<List<RecurringTransaction>> getRecurringTransactionByUserId(int userId) {
        return recurringTransactionRepository.getRecurringTransactionByUserId(userId);
    }

    @Override
    public void createRecurringTransaction(RecurringTransaction recurringTransaction, Wallet walletSender,
                                           User userSender, Wallet walletReceiver,
                                           User userReceiver) {
        if (recurringTransaction.getStartDate().isBefore(LocalDate.now())
                && recurringTransaction.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date and end date can not be in the past");
        }

        transactionService.createTransaction(recurringTransaction, walletSender, userSender, walletReceiver, userReceiver);
    }

    @Override
    public void updateRecurringTransaction(RecurringTransaction recurringTransaction, User user) {
        checkBlockOrDeleteUser(user, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(recurringTransaction.getWalletSender(), user, ERROR_TRANSACTION);

        if (recurringTransaction.getStartDate().isBefore(LocalDate.now())
                && recurringTransaction.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date and end date can not be in the past");
        }

        recurringTransactionRepository.update(recurringTransaction);
    }

    @Override
    public void cancelRecurringTransaction(RecurringTransaction recurringTransaction, User user) {
        checkBlockOrDeleteUser(user, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        checkPermissionExistingUsersInWallet(recurringTransaction.getWalletSender(), user, ERROR_TRANSACTION);
        recurringTransaction.setEndDate(LocalDate.now());
        recurringTransactionRepository.update(recurringTransaction);
    }

    @Override
    @Scheduled(cron = "0 */3 * * * *")
    public void executeRecurringTransaction() {
        LocalDate currentDate = LocalDate.now();
        List<RecurringTransaction> recurringTransactions = getAllRecurringTransactions();

        for (RecurringTransaction transaction : recurringTransactions) {
            if (isDueToday(transaction, currentDate)) {
                processRecurringTransaction(transaction.getTransactionId());
            }
        }
    }

    private boolean isDueToday(RecurringTransaction transaction, LocalDate currentDate) {
        LocalDate startDate = transaction.getStartDate();
        LocalDate endDate = transaction.getEndDate();

        if (currentDate.isBefore(startDate) || currentDate.isAfter(endDate)) {
            return false;
        }

        if (transaction.getFrequency() == Frequency.DAILY) {
            return true;
        }

        if (transaction.getFrequency() == Frequency.WEEKLY) {
            long daysDifference = ChronoUnit.DAYS.between(startDate, currentDate);
            return daysDifference % 7 == 0;
        }

        if (transaction.getFrequency() == Frequency.MONTHLY) {
            return startDate.getDayOfMonth() == currentDate.getDayOfMonth();
        }

        return false;
    }

    private void processRecurringTransaction(int transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        Transaction newTransaction = transactionMapper.fromDtoRecurring(transaction);

        if (isValidRequestEnoughMoney(newTransaction, newTransaction.getWalletSender())) {
            newTransaction.getTransactionsStatus().setId(Status.COMPLETED.ordinal() + 1);
            newTransaction.getTransactionsStatus().setTransactionStatus(Status.COMPLETED);
            transactionService.createRecurringTransaction(newTransaction);

            newTransaction.getWalletSender().setBalance(newTransaction.getWalletSender().getBalance()
                    .subtract(transaction.getAmount()));
            newTransaction.getWalletSender().getSentTransactions().add(newTransaction);
            newTransaction.getWalletReceiver().setBalance(newTransaction.getWalletReceiver().getBalance()
                    .add(transaction.getAmount()));
            newTransaction.getWalletReceiver().getReceiverTransactions().add(newTransaction);
            walletService.updateRecurringTransaction(newTransaction.getWalletSender());
            walletService.updateRecurringTransaction(newTransaction.getWalletReceiver());
        } else {
            newTransaction.getTransactionsStatus().setId(Status.FAILED.ordinal() + 1);
            newTransaction.getTransactionsStatus().setTransactionStatus(Status.FAILED);
            transactionService.createRecurringTransaction(newTransaction);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);

        }
    }

    private boolean isValidRequestEnoughMoney(Transaction transaction, Wallet walletSender) {
        BigDecimal balanceAfterTransfer = walletSender.getBalance().subtract(transaction.getAmount());
        return balanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;
    }
}
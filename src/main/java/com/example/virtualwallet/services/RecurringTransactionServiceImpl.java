package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.enums.CardStatus;
import com.example.virtualwallet.models.enums.Interval;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.RecurringTransactionRepository;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.Messages.ERROR_INSUFFICIENT_BALANCE;

@Service
public class RecurringTransactionServiceImpl implements RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionService transactionService;
    private final WalletService walletService;

    @Autowired
    public RecurringTransactionServiceImpl(RecurringTransactionRepository recurringTransactionRepository,
                                           TransactionService transactionService, WalletService walletService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionService = transactionService;
        this.walletService = walletService;
    }
@Override
    public List<RecurringTransaction> getAllRecurringTransactions(){
    return recurringTransactionRepository.getAllRecurringTransactions()
            .orElseThrow(() -> new EntityNotFoundException("Recurring Transactions"));
}


    @Override
    public void createRecurringTransaction(RecurringTransaction recurringTransaction, Wallet walletSender,
                                           User userSender, Wallet walletReceiver,
                                           User userReceiver) {

        if (recurringTransaction.getStartDate().isBefore(LocalDate.now()) && recurringTransaction.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date and end date can not be in the past");
        }
        transactionService.requestMoney(recurringTransaction, walletReceiver, userReceiver);

    }

    @Override
    @Scheduled(cron = "0 0 0 * * *") // Execute daily at midnight
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

            // Check if the current date is within the start and end date range
            if (currentDate.isBefore(startDate) || currentDate.isAfter(endDate)) {
                return false;
            }

            // Check if the interval is 1 day
            if (transaction.getIntervals().equals(Interval.DAILY)) {
                return true;
            }

            // Calculate the interval in days
            int interval = 0;
            switch (transaction.getIntervals()) {
                case DAILY:
                    interval = 1;
                    break;
                case WEEKLY:
                    interval = 7;
                    break;
                case MONTHLY:
                    interval = 30; // This is a rough approximation, consider using java.time.Period
                    break;
                default:
                    break;
            }
            long daysDifference = ChronoUnit.DAYS.between(startDate, currentDate);
            // Check if the current date is within the interval of the transaction
            return daysDifference % interval == 0;
        }

        private void processRecurringTransaction( int transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
            if (isValidRequestEnoughMoney(transaction, transaction.getWalletSender())) {
                transaction.getTransactionsStatus().setId(Status.COMPLETED.ordinal());
                transaction.getTransactionsStatus().setTransactionStatus(Status.COMPLETED);
                transactionService.createRecurringTransaction(transaction);

                transaction.getWalletSender().setBalance(transaction.getWalletSender().getBalance().subtract(transaction.getAmount()));
                transaction.getWalletSender().getSentTransactions().add(transaction);
                transaction.getWalletReceiver().setBalance(transaction.getWalletReceiver().getBalance().add(transaction.getAmount()));
                transaction.getWalletReceiver().getReceiverTransactions().add(transaction);
                walletService.updateRecurringTransaction(transaction.getWalletSender());
                walletService.updateRecurringTransaction(transaction.getWalletReceiver());
            }else{
                transaction.getTransactionsStatus().setId(Status.FAILED.ordinal());
                transaction.getTransactionsStatus().setTransactionStatus(Status.FAILED);
                transactionService.createRecurringTransaction(transaction);
                throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);

            }
        }

    private boolean isValidRequestEnoughMoney(Transaction transaction, Wallet walletSender) {

        BigDecimal balanceAfterTransfer = walletSender.getBalance().subtract(transaction.getAmount());
        return balanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;
    }



}

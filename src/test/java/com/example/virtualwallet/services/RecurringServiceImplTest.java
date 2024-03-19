package com.example.virtualwallet.services;

import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.repositories.contracts.RecurringTransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.helpers.createMockRecurringTransaction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecurringServiceImplTest {
    @Mock
    private RecurringTransactionRepository mockRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private RecurringTransactionServiceImpl recurringTransactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testExecuteRecurringTransaction_WithRecurringTransactions() {
        // Mock the behavior of the repository
        List<RecurringTransaction> recurringTransactions = List.of(createMockRecurringTransaction());
        when(mockRepository.getAllRecurringTransactions()).thenReturn(Optional.of(recurringTransactions));

        // Call the method to be tested
        recurringTransactionService.executeRecurringTransaction();

        // Verify that processRecurringTransaction is called for each due transaction
        Mockito.verify(transactionService, times(1)).createRecurringTransaction(recurringTransactions.get(0));
    }

    @Test
    public void testExecuteRecurringTransaction_WithoutRecurringTransactions() {
        // Mock the behavior of the repository
        when(mockRepository.getAllRecurringTransactions()).thenReturn(Optional.empty());

        // Call the method to be tested
        //  recurringTransactionService.executeRecurringTransaction();

        // Verify that processRecurringTransaction is never called when there are no transactions
        Mockito.verify(transactionService, Mockito.never()).createRecurringTransaction(Mockito.any());
    }

    // Add more tests for other methods as needed
}


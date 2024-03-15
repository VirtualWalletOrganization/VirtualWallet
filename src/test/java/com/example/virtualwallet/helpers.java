package com.example.virtualwallet;

import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.enums.*;
import com.example.virtualwallet.utils.UserFilterOptions;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class helpers {
    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("MockUsername");
        mockUser.setPassword("MockPassword");
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mock@user.com");
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setPhoneNumber("3333333333");
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.ADMIN);
        mockUser.setUsersRole(usersRole);

        return mockUser;
    }

    public static UserFilterOptions createMockUserFilterOptions() {
        return new UserFilterOptions(
                createMockUser().getUsername(),
                createMockUser().getFirstName(),
                createMockUser().getLastName(),
                createMockUser().getEmail(),
                Role.USER,
                UserStatus.ACTIVE,
                "sort",
                "order");
    }

    public static Card createMockCard() {
        Card mockCard = new Card();
        mockCard.setId(1);
        mockCard.setCardsType(new CardsType());
        mockCard.setUser(createMockUser());
        mockCard.setCardNumber("1234567891234567");
        mockCard.setExpirationDate(LocalDateTime.now());
        mockCard.setCardHolder("MockFirstName MockLastName");
        mockCard.setCheckNumber("123");
        mockCard.setCurrency("USD");
        mockCard.setCardStatus(CardStatus.ACTIVE);

        return mockCard;
    }

    public static Wallet createMockWallet() {
        Wallet mockWallet = new Wallet();
        mockWallet.setId(1);
        mockWallet.setCreator(new User());
        mockWallet.setBalance(BigDecimal.ZERO);
        mockWallet.setCurrency("USD");
        mockWallet.setWalletsType(new WalletsType());
        mockWallet.setDefault(false);
        mockWallet.setDeleted(false);
        mockWallet.setOverdraftEnabled(true);
        mockWallet.setSavingEnabled(true);
        return mockWallet;
    }

    public static RecurringTransaction createMockRecurringTransaction() {
        RecurringTransaction mockTransaction = new RecurringTransaction();
        mockTransaction.setTransactionId(1);
        mockTransaction.setWalletSender(createMockWallet());
        Wallet wallet = createMockWallet();
        wallet.setId(2);
        mockTransaction.setWalletReceiver(wallet);
        mockTransaction.setAmount(BigDecimal.TEN);
        mockTransaction.setCurrency("USD");
        mockTransaction.setDate(Timestamp.valueOf(LocalDateTime.now()));
        mockTransaction.setTransactionsStatus(new TransactionsStatus());
        mockTransaction.getTransactionsStatus().setId(1);
        mockTransaction.getTransactionsStatus().setTransactionStatus(Status.PENDING_RECURRING_REQUEST);
        mockTransaction.setDescription("Mock Recurring Transaction");
        mockTransaction.setTransactionsType(new TransactionsType());
        mockTransaction.getTransactionsType().setId(2);
        mockTransaction.getTransactionsType().setTransactionType(TransactionType.RECURRING);
        mockTransaction.setIntervals(Interval.DAILY);
        mockTransaction.setStartDate(LocalDate.now());
        mockTransaction.setEndDate(LocalDate.now().plusMonths(6));
        return mockTransaction;
    }
}


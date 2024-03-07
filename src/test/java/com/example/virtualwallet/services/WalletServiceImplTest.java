package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.UsersRole;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        walletService = new WalletServiceImpl(walletRepository, userService);
    }

    @Test
    void getAll_AdminUser_ReturnsListOfWallets() {
        User adminUser = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setId(1);
        usersRole.setRole(Role.ADMIN);
        adminUser.setUsersRole(usersRole);

        List<Wallet> expectedWallets = new ArrayList<>();
        when(walletRepository.getAll()).thenReturn(expectedWallets);

        List<Wallet> actualWallets = walletService.getAll(adminUser);

        assertEquals(expectedWallets, actualWallets);
        verify(walletRepository, times(1)).getAll();
    }

    @Test
    void getAllUsersByWalletId_ValidWalletId_ReturnsListOfUsers() {
        int walletId = 1;
        List<User> expectedUsers = new ArrayList<>();
        when(walletRepository.getAllUsersByWalletId(walletId)).thenReturn(Optional.of(expectedUsers));

        List<User> actualUsers = walletService.getAllUsersByWalletId(walletId);

        assertEquals(expectedUsers, actualUsers);
        verify(walletRepository, times(1)).getAllUsersByWalletId(walletId);
    }

    @Test
    void getAllUsersByWalletId_InvalidWalletId_ThrowsEntityNotFoundException() {
        int walletId = 1;
        when(walletRepository.getAllUsersByWalletId(walletId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.getAllUsersByWalletId(walletId));
        verify(walletRepository, times(1)).getAllUsersByWalletId(walletId);
    }

//    @Test
//    void getWalletById_ValidWalletId_ReturnsWallet() {
//        int walletId = 1;
//        int userId = 1;
//        User user = new User();
//        user.setId(userId);
//        Wallet expectedWallet = new Wallet();
//        expectedWallet.setId(walletId);
//        when(userService.getById(userId)).thenReturn(user);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.of(expectedWallet));
//
//        Wallet actualWallet = walletService.getWalletById(walletId, userId);
//
//        assertEquals(expectedWallet, actualWallet);
//        verify(walletRepository, times(1)).getWalletById(walletId);
//    }
//
//    @Test
//    void getWalletById_InvalidWalletId_ThrowsEntityNotFoundException() {
//        int walletId = 1;
//        int userId = 1;
//        User user = new User();
//        user.setId(userId);
//        when(userService.getById(userId)).thenReturn(user);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> walletService.getWalletById(walletId, userId));
//        verify(walletRepository, times(1)).getWalletById(walletId);
//    }
//
//    @Test
//    void getWalletById_UnauthorizedUser_ThrowsAuthorizationException() {
//        int walletId = 1;
//        int userId = 1;
//        User user = new User();
//        user.setId(userId);
//        when(userService.getById(userId)).thenReturn(user);
//        Wallet wallet = new Wallet();
//        wallet.setId(walletId);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.of(wallet));
//
//        assertThrows(AuthorizationException.class, () -> walletService.getWalletById(walletId, userId + 1));
//        verify(walletRepository, never()).getWalletById(walletId);
//    }

    @Test
    void getWalletByCardId_ValidCardId_ReturnsWallet() {
        int cardId = 123456;
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Wallet expectedWallet = new Wallet();
        when(userService.getById(userId)).thenReturn(user);
        when(walletRepository.getWalletByCardId(cardId)).thenReturn(Optional.of(expectedWallet));

        Wallet actualWallet = walletService.getWalletByCardId(cardId, userId);

        assertEquals(expectedWallet, actualWallet);
        verify(walletRepository, times(1)).getWalletByCardId(cardId);
    }

    @Test
    void getWalletByCardId_InvalidCardId_ThrowsEntityNotFoundException() {
        int cardId = 123456;
        int userId = 1;
        User user = new User();
        user.setId(userId);
        when(userService.getById(userId)).thenReturn(user);
        when(walletRepository.getWalletByCardId(cardId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.getWalletByCardId(cardId, userId));
        verify(walletRepository, times(1)).getWalletByCardId(cardId);
    }

    @Test
    void getDefaultWallet_ValidUserId_ReturnsDefaultWallet() {
        int recipientUserId = 1;
        Wallet expectedWallet = new Wallet();
        when(walletRepository.getDefaultWallet(recipientUserId)).thenReturn(Optional.of(expectedWallet));

        Wallet actualWallet = walletService.getDefaultWallet(recipientUserId);

        assertEquals(expectedWallet, actualWallet);
        verify(walletRepository, times(1)).getDefaultWallet(recipientUserId);
    }

    @Test
    void getDefaultWallet_InvalidUserId_ThrowsEntityNotFoundException() {
        int recipientUserId = 1;
        when(walletRepository.getDefaultWallet(recipientUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.getDefaultWallet(recipientUserId));
        verify(walletRepository, times(1)).getDefaultWallet(recipientUserId);
    }

    @Test
    void getAllWalletsByCreatorId_ValidCreatorId_ReturnsListOfWallets() {
        int creatorId = 1;
        List<Wallet> expectedWallets = new ArrayList<>();
        when(walletRepository.getAllWalletsByCreatorId(creatorId)).thenReturn(Optional.of(expectedWallets));

        List<Wallet> actualWallets = walletService.getAllWalletsByCreatorId(creatorId);

        assertEquals(expectedWallets, actualWallets);
        verify(walletRepository, times(1)).getAllWalletsByCreatorId(creatorId);
    }

    @Test
    void getAllWalletsByCreatorId_InvalidCreatorId_ThrowsEntityNotFoundException() {
        int creatorId = 1;
        when(walletRepository.getAllWalletsByCreatorId(creatorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.getAllWalletsByCreatorId(creatorId));
        verify(walletRepository, times(1)).getAllWalletsByCreatorId(creatorId);
    }

    @Test
    void getAllWalletsByUserId_ValidUserId_ReturnsListOfWallets() {
        int userId = 1;
        List<Wallet> expectedWallets = new ArrayList<>();
        when(walletRepository.getAllWalletsByUserId(userId)).thenReturn(Optional.of(expectedWallets));

        List<Wallet> actualWallets = walletService.getAllWalletsByUserId(userId);

        assertEquals(expectedWallets, actualWallets);
        verify(walletRepository, times(1)).getAllWalletsByUserId(userId);
    }

    @Test
    void getAllWalletsByUserId_InvalidUserId_ThrowsEntityNotFoundException() {
        int userId = 1;
        when(walletRepository.getAllWalletsByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> walletService.getAllWalletsByUserId(userId));
        verify(walletRepository, times(1)).getAllWalletsByUserId(userId);
    }

//    @Test
//    void create_ValidWalletAndUser_CreatesWallet() {
//        Wallet wallet = new Wallet();
//        User user = new User();
//        user.setCreatedWallets(new HashSet<>());
//        when(walletRepository.create(wallet)).thenReturn(wallet);
//
//        Wallet createdWallet = walletService.create(wallet, user);
//
//        assertNotNull(createdWallet);
//        assertEquals(user, createdWallet.getCreator());
//        verify(walletRepository, times(1)).create(wallet);
//        verify(userService, times(1)).updateUser(user, user);
//    }
//
//    @Test
//    void createWhenRegistering_ValidWalletAndUser_CreatesWallet() {
//        Wallet wallet = new Wallet();
//        User user = new User();
//        user.setUsername("testUser");
//        when(userService.getByUsername(user.getUsername())).thenReturn(user);
//        when(walletRepository.create(wallet)).thenReturn(wallet);
//        when(walletRepository.getByCreatorIdWhenRegistering(user.getId())).thenReturn(wallet);
//
//        walletService.createWhenRegistering(wallet, user);
//
//        assertNotNull(wallet.getCreator());
//        assertTrue(wallet.getDefault());
//        assertTrue(wallet.getUsers().stream().anyMatch(u -> u.equals(user)));
//        verify(walletRepository, times(1)).create(wallet);
//        verify(walletRepository, times(1)).getByCreatorIdWhenRegistering(user.getId());
//        verify(userService, times(1)).updateUser(user, user);
//    }
//
//    @Test
//    void update_ValidWalletAndUser_UpdatesWallet() {
//        Wallet wallet = new Wallet();
//        User user = new User();
//        user.setId(1);
//        doNothing().when(walletRepository).update(wallet);
//
//        walletService.update(wallet, user);
//
//        verify(walletRepository, times(1)).update(wallet);
//    }

    @Test
    void updateRecurringTransaction_ValidWallet_UpdatesWallet() {
        Wallet wallet = new Wallet();
        doNothing().when(walletRepository).update(wallet);

        walletService.updateRecurringTransaction(wallet);

        verify(walletRepository, times(1)).update(wallet);
    }

//    @Test
//    void delete_ValidWalletIdAndUser_DeletesWallet() {
//        int walletId = 1;
//        int userId = 1;
//        Wallet wallet = new Wallet();
//        wallet.setId(walletId);
//        User user = new User();
//        user.setId(userId);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.of(wallet));
//
//        walletService.delete(walletId, user);
//
//        verify(walletRepository, times(1)).delete(wallet);
//    }
//
//    @Test
//    void delete_InvalidWalletIdAndUser_ThrowsEntityNotFoundException() {
//        int walletId = 1;
//        int userId = 1;
//        User user = new User();
//        user.setId(userId);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> walletService.delete(walletId, user));
//        verify(walletRepository, never()).delete(any());
//    }
//
//    @Test
//    void addUsersToWallet_ValidWalletIdAndUserToAddId_AddsUserToWallet() {
//        int walletId = 1;
//        int userToAddId = 2;
//        int userId = 1;
//        Wallet wallet = new Wallet();
//        wallet.setId(walletId);
//        User executingUser = new User();
//        executingUser.setId(userId);
//        User userToAdd = new User();
//        userToAdd.setId(userToAddId);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.of(wallet));
//        when(userService.getById(userToAddId)).thenReturn(userToAdd);
//
//        walletService.addUsersToWallet(walletId, userToAddId, executingUser);
//
//        assertTrue(wallet.getUsers().contains(userToAdd));
//        verify(walletRepository, times(1)).update(wallet);
//        verify(userService, times(1)).updateUser(userToAdd, userToAdd);
//    }
//
//    @Test
//    void removeUsersFromWallet_ValidWalletIdAndUserId_RemovesUserFromWallet() {
//        int walletId = 1;
//        int userId = 2;
//        int executingUserId = 1;
//        Wallet wallet = new Wallet();
//        wallet.setId(walletId);
//        User executingUser = new User();
//        executingUser.setId(executingUserId);
//        User userToRemove = new User();
//        userToRemove.setId(userId);
//        wallet.getUsers().add(userToRemove);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.of(wallet));
//
//        walletService.removeUsersFromWallet(walletId, userId, executingUser);
//
//        assertFalse(wallet.getUsers().contains(userToRemove));
//        verify(walletRepository, times(1)).update(wallet);
//        verify(userService, times(1)).updateUser(userToRemove, userToRemove);
//    }
//
//    @Test
//    void removeUsersFromWallet_UnauthorizedUser_ThrowsAuthorizationException() {
//        int walletId = 1;
//        int userId = 2;
//        int executingUserId = 1;
//        Wallet wallet = new Wallet();
//        wallet.setId(walletId);
//        User executingUser = new User();
//        executingUser.setId(executingUserId);
//        User userToRemove = new User();
//        userToRemove.setId(userId);
//        wallet.getUsers().add(userToRemove);
//        when(walletRepository.getWalletById(walletId)).thenReturn(Optional.of(wallet));
//
//        assertThrows(AuthorizationException.class, () -> walletService.removeUsersFromWallet(walletId, userId, new User()));
//        assertTrue(wallet.getUsers().contains(userToRemove));
//        verify(walletRepository, never()).update(wallet);
//        verify(userService, never()).updateUser(userToRemove, userToRemove);
//    }
}
package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.WalletRole;
import com.example.virtualwallet.models.enums.WalletType;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.example.virtualwallet.utils.CheckPermissions.*;
import static com.example.virtualwallet.utils.Messages.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, UserService userService) {
        this.walletRepository = walletRepository;
        this.userService = userService;
    }

    @Override
    public List<Wallet> getAll(User user) {
        checkAccessPermissionsAdmin(user, WALLET_ERROR_MESSAGE);
        return walletRepository.getAll();
    }

//    @Override
//    public List<User> getAllUsersByWalletId(int walletId) {
//        getWalletById(walletId);
//        return walletRepository.getAllUsersByWalletId(walletId)
//                .orElseThrow(() -> new EntityNotFoundException("Users", "wallet id", String.valueOf(walletId)));
//    }

//    @Override
//    public Wallet getWalletById(int walletId, int userId) {
////        if (walletRepository.existsUserWithWallet(userId, walletId).isEmpty()) {
////            throw new EntityNotFoundException("Users", "wallet id", String.valueOf(walletId));
////        }
//
//        User user = userService.getById(userId);
//        user.getWallets().stream()
//                .filter(wallet -> wallet.getId() == walletId)
//                .findFirst()
//                .orElseThrow(() -> new EntityNotFoundException("Wallet", "creator", String.valueOf(userId)));
//
//        return walletRepository.getWalletById(walletId)
//                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));
//    }

    @Override
    public Wallet getWalletById(int walletId, int userId) {
        User user = userService.getById(userId);
        user.getCreatedWallets().stream()
                .filter(wallet -> wallet.getId() == walletId)
                .findFirst()
                .orElseThrow(() -> new AuthorizationException(SEARCH_WALLET_ERROR_MESSAGE));

        return walletRepository.getWalletById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));
    }

    @Override
    public Wallet getDefaultWallet(int recipientUserId) {
        return walletRepository.getDefaultWallet(recipientUserId)
                .orElseThrow(() -> new EntityNotFoundException("Recipient's wallet"));
    }

    @Override
    public List<Wallet> getByCreatorId(int creatorId) {
        return walletRepository.getByCreatorId(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Wallets"));
    }

    @Override
    public Wallet create(Wallet wallet, User user) {
        wallet.setCreator(user);

        if (wallet.getWalletsType().getWalletType().equals(WalletType.JOINT)) {
            User userCreator = wallet.getCreator();
            userCreator.getWalletsRole().setWalletRole(WalletRole.ADMIN);
            wallet.setCreator(userCreator);
        }

        if (user.getCreatedWallets().size() == 1) {
            wallet.setDefault(true);
        }

        return walletRepository.create(wallet);
    }

    @Override
    public void update(Wallet wallet, User user) {
        checkAccessPermissionWalletUser(wallet, user, MODIFY_WALLET_ERROR_MESSAGE);
        walletRepository.update(wallet);
    }

    @Override
    public void delete(int walletId, User user) {
        Wallet wallet = getWalletById(walletId, user.getId());
        checkAccessPermissionWalletUser(wallet, user, MODIFY_WALLET_ERROR_MESSAGE);
        walletRepository.delete(wallet);
    }

    @Override
    public void addUsersToWallet(int walletId, User userToAdd, User executingUser) {
        Wallet wallet = getWalletById(walletId, executingUser.getId());

        if (!wallet.getWalletsType().getWalletType().equals(WalletType.JOINT)) {
            throw new EntityNotFoundException("Joint wallet", "id", String.valueOf(walletId));
        }

        userService.getById(userToAdd.getId());

        checkUserWalletAdmin(wallet, executingUser, ADD_USER_TO_WALLET);

        Set<User> existingUsers = wallet.getUsers();

        if (existingUsers.contains(userToAdd)) {
            throw new DuplicateEntityException("User", "id", "one of the provided user IDs");
        }

        wallet.getUsers().add(userToAdd);
        walletRepository.update(wallet);
        userToAdd.getWallets().add(wallet);
        userService.updateUser(userToAdd, userToAdd);
    }

    @Override
    public void removeUsersFromWallet(int walletId, int userId, User executingUser) {
        Wallet wallet = getWalletById(walletId, executingUser.getId());
        User userToRemove = userService.getById(userId);

        checkUserWalletAdmin(wallet, executingUser, REMOVE_USER_FROM_WALLET);

        wallet.getUsers().remove(userToRemove);
        walletRepository.update(wallet);
        userToRemove.getWallets().remove(wallet);
        userService.updateUser(userToRemove, userToRemove);
    }
}
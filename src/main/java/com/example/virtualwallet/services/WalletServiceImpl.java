package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.WalletsRole;
import com.example.virtualwallet.models.enums.WalletRole;
import com.example.virtualwallet.models.enums.WalletType;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Wallet> getAllWalletsByUserId(User user) {
        List<Wallet> wallets = walletRepository.getAllWalletsByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cards"));
        checkPermissionShowingWalletsByUser(wallets, user, SEARCH_WALLET_ERROR_MESSAGE);
        return wallets;

    }

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
//        User user = userService.getById(userId);
//        user.getCreatedWallets().stream()
//                .filter(wallet -> wallet.getId() == walletId)
//                .findFirst()
//                .orElseThrow(() -> new AuthorizationException(SEARCH_WALLET_ERROR_MESSAGE));
//
//        return walletRepository.getWalletById(walletId)
//                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));

        User user = userService.getById(userId);
        Wallet wallet = walletRepository.getWalletById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));
        checkPermissionExistingUsersInWallet(wallet, user, SEARCH_WALLET_ERROR_MESSAGE);
//        if (!user.getCreatedWallets().stream().anyMatch(w -> w.getId() == walletId)) {
//            throw new AuthorizationException(SEARCH_WALLET_ERROR_MESSAGE);
//        }

        return wallet;
    }

    @Override
    public List<Wallet> getWalletsByCardId(int cardId, int userId) {
        userService.getById(userId);
        return walletRepository.getWalletsByCardId(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", " card id", String.valueOf(cardId)));
    }

    @Override
    public Wallet getDefaultWallet(int recipientUserId) {
        return walletRepository.getDefaultWallet(recipientUserId)
                .orElseThrow(() -> new EntityNotFoundException("Default's wallet"));
    }

    @Override
    public List<Wallet> getAllWalletsByCreatorId(int creatorId) {
        return walletRepository.getAllWalletsByCreatorId(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Wallets"));
    }


    @Override
    public Wallet create(Wallet wallet, User user) {
        if (wallet.getWalletsType().getWalletType().equals(WalletType.JOINT)) {
            User userCreator = wallet.getCreator();
            WalletsRole walletsRole = new WalletsRole();
            walletsRole.setId(WalletRole.ADMIN.ordinal() + 1);
            walletsRole.setWalletRole(WalletRole.ADMIN);
            userCreator.setWalletsRole(walletsRole);
            wallet.setCreator(userCreator);
        }
        if(wallet.getDefault()){
            checkDefaultWallets(wallet, user);
        }
        user.getWallets().add(wallet);
        Wallet walletToAdd = walletRepository.create(wallet);
        userService.updateUser(user, user);
        return walletToAdd;
    }

    @Override
    public void update(Wallet walletToUpdate, User user) {
        checkPermissionExistingUsersInWallet(walletToUpdate, user, MODIFY_WALLET_ERROR_MESSAGE);
            checkDefaultWallets(walletToUpdate, user);
            walletRepository.update(walletToUpdate);

    }

    @Override
    public void updateRecurringTransaction(Wallet wallet) {
        walletRepository.update(wallet);
    }

    @Override
    public void delete(Wallet walletToDelete, User currentUser) {
        if (walletToDelete.getWalletsType().getWalletType() == WalletType.JOINT) {
            checkUserWalletAdmin(walletToDelete, currentUser, DELETE_WALLET);
        }
        walletToDelete.setDeleted(true);
        List<User> users = userService.getAllUsersByWalletId(walletToDelete.getId());
        for (User user : users) {
            user.getWallets().forEach(wallet -> {
                if (wallet.getId() == walletToDelete.getId()) {
                    wallet.setDeleted(true);
                    user.getWallets().remove(wallet);
                    wallet.getUsers().remove(user);
                    walletRepository.update(wallet);
                }
            });

            walletRepository.update(walletToDelete);
        }
    }

    @Override
    public void addUsersToWallet(int walletId, int userToAddId, User executingUser) {
        Wallet wallet = getWalletById(walletId, executingUser.getId());
        User userToAdd = userService.getById(userToAddId);
        if (!wallet.getWalletsType().getWalletType().equals(WalletType.JOINT)) {
            throw new EntityNotFoundException("Joint wallet", "id", String.valueOf(walletId));
        }

        checkUserWalletAdmin(wallet, executingUser, ADD_USER_TO_WALLET);
        if (wallet.getUsers().contains(userToAdd)) {
            throw new DuplicateEntityException("User", "id", "one of the provided user IDs");
        }
        userToAdd.getWallets().stream()
                .filter(w -> wallet.getId() == walletId && wallet.getDeleted())
                .forEach(w -> wallet.setDeleted(false));


        wallet.getUsers().add(userToAdd);
        walletRepository.update(wallet);
        userToAdd.getWallets().add(wallet);
        userService.updateUser(userToAdd, userToAdd);
    }

    @Override
    public void removeUsersFromWallet(int walletId, int userId, User executingUser) {
        Wallet wallet = getWalletById(walletId, executingUser.getId());
        User userToRemove = userService.getById(userId);
        if(executingUser.getId()==userId){
            throw new AuthorizationException(REMOVE_YOURSELF_FROM_WALLET);
        }
        checkUserWalletAdmin(wallet, executingUser, REMOVE_USER_FROM_WALLET);

        wallet.getUsers().remove(userToRemove);
        walletRepository.update(wallet);
        userToRemove.getWallets().remove(wallet);
        userService.updateUser(userToRemove, userToRemove);
    }

    private void checkDefaultWallets(Wallet wallet, User user) {
        Optional<Wallet> currentDefaultWallet = walletRepository.getDefaultWallet(user.getId());
        if (currentDefaultWallet.isPresent()){
            if (currentDefaultWallet.get().getId() != wallet.getId()) {
                currentDefaultWallet.get().setDefault(false);
                walletRepository.update(currentDefaultWallet.get());
                wallet.setDefault(true);
            }
//            } else {
//                throw new DuplicateEntityException("Wallet", "id", String.valueOf(wallet.getId()),
//                        "has already been set as default.");
//            }
        }else {
            wallet.setDefault(true);
        }
    }
}
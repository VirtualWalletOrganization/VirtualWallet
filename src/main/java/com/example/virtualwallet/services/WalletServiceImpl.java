package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.WalletRole;
import com.example.virtualwallet.models.enums.WalletType;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.example.virtualwallet.utils.CheckPermissions.checkUserWalletAdmin;
import static com.example.virtualwallet.utils.Messages.ADD_USER_TO_WALLET;
import static com.example.virtualwallet.utils.Messages.REMOVE_USER_FROM_WALLET;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Wallet> getAll() {
        return walletRepository.getAll();
    }

    @Override
    public Wallet getWalletById(int walletId) {
        return walletRepository.getWalletById(walletId);
    }

    @Override
    public List<Wallet> getByCreatorId(int creatorId) {
        return walletRepository.getByCreatorId(creatorId);
    }

    @Override
    public Wallet create(Wallet wallet) {
        if (wallet.getWalletType().equals(WalletType.JOINT)) {
            User userCreator = wallet.getCreator();
            userCreator.setWalletRole(WalletRole.ADMIN);
            wallet.setCreator(userCreator);
        }

        return walletRepository.create(wallet);
    }

    @Override
    public void update(Wallet wallet) {
        walletRepository.update(wallet);
    }

    @Override
    public void delete(Wallet wallet) {
        walletRepository.delete(wallet);
    }

    @Override
    public void addUsersToWallet(int walletId, int userId, User user) {
        Wallet wallet = walletRepository.getWalletById(walletId);

        checkUserWalletAdmin(wallet, user, ADD_USER_TO_WALLET);

        Set<User> existingUsers = wallet.getUsers();
        User userToAdd = userRepository.getById(userId);

        if (existingUsers.contains(userToAdd)) {
            throw new DuplicateEntityException("User", "id", "one of the provided user IDs");
        }

        wallet.getUsers().add(userToAdd);
        userToAdd.getWallets().add(wallet);
        walletRepository.update(wallet);
    }

    @Override
    public void removeUsersFromWallet(int walletId, int userId, User user) {
        Wallet wallet = walletRepository.getWalletById(walletId);

        checkUserWalletAdmin(wallet, user, REMOVE_USER_FROM_WALLET);

        User userToRemove = userRepository.getById(userId);

        wallet.getUsers().remove(userToRemove);
        userToRemove.getWallets().remove(wallet);
        walletRepository.update(wallet);
    }
}
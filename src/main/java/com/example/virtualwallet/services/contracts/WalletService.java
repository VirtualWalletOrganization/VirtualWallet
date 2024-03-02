package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    List<Wallet> getAll(User user);

//    List<User> getAllUsersByWalletId(int walletId);

    Wallet getWalletById(int walletId, int userId);

    Wallet getDefaultWallet(int recipientUserId);

    List<Wallet> getByCreatorId(int creatorId);

    Wallet create(Wallet wallet, User user);

    void update(Wallet wallet, User user);

    void delete(Wallet wallet, User user);

    void addUsersToWallet(int walletId, int userId, User user);

    void removeUsersFromWallet(int walletId, int userId, User user);
}
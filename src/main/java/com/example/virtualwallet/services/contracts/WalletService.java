package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WalletService {

    List<Wallet> getAll();

    Wallet getWalletById(int walletId);

    Wallet getDefaultWallet(int recipientUserId);

    List<Wallet> getByCreatorId(int creatorId);

    Wallet create(Wallet wallet);

    void update(Wallet wallet);

    void delete(Wallet wallet);

    void addUsersToWallet(int walletId, int userId, User user);

    void removeUsersFromWallet(int walletId, int userId, User user);

    void updateBalance(int walletId, BigDecimal newBalance);
}
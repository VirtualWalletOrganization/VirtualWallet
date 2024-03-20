package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletService {

    List<Wallet> getAll(User user);

    List<Wallet> getAllWalletsByUserId(User user);

    Wallet getWalletById(int walletId, int userId);

    List<Wallet> getWalletsByCardId(int cardId, int userId);

    Wallet getDefaultWallet(int recipientUserId);

    List<Wallet> getAllWalletsByCreatorId(int creatorId);

    Wallet create(Wallet wallet, User user);

    void update(Wallet wallet, User user);

    void updateRecurringTransaction(Wallet wallet);

    void delete(Wallet walletToDelete, User user);

    void addUsersToWallet(int walletId, int userId, User user);

    void removeUsersFromWallet(int walletId, int userId, User user);

    String moneyFromCardToWallet(Transfer transfer, Wallet receiverWallet, User user, Card card);
}
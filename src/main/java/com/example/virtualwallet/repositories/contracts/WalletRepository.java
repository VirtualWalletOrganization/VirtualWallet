package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {

    List<Wallet> getAll();

    Optional<Wallet> getWalletById(int walletId);

    Optional<List<Wallet>> getWalletsByCardId(int cardId);

    Optional<Wallet> getDefaultWallet(int recipientUserId);

    Optional<List<Wallet>> getAllWalletsByCreatorId(int creatorId);

    Optional<List<Wallet>> getAllWalletsByUserId(int userId);

    Wallet create(Wallet wallet);

    void update(Wallet wallet);

    void delete(Wallet wallet);
}
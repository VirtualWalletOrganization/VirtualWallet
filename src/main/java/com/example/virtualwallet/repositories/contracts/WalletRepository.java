package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepository {

    List<Wallet> getAll();

    Optional<List<User>> getAllUsersByWalletId(int walletId);

    Optional<Wallet> getWalletById(int walletId);

    Optional<Wallet> getWalletByCardId(int cardId);

    Optional<Wallet> getDefaultWallet(int recipientUserId);

    Optional<List<Wallet>> getByCreatorId(int creatorId);

    Wallet getByCreatorIdWhenRegistering(int creatorId);

    Wallet create(Wallet wallet);

    void update(Wallet wallet);

    void delete(Wallet wallet);
}
package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Wallet;

import java.util.List;

public interface WalletRepository {

    List<Wallet> findAll();

    Wallet getWalletById(int walletId);

    List<Wallet> findByCreatorId(int creatorId);

    Wallet create(Wallet wallet);

    void update(Wallet wallet);

    void delete(Wallet wallet);
}
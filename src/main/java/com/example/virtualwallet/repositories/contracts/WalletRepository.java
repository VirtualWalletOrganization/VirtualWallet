package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletRepository {

    List<Wallet> getAll();

    Wallet getWalletById(int walletId);

    List<Wallet> getByCreatorId(int creatorId);

    Wallet create(Wallet wallet);

    void update(Wallet wallet);

    void delete(Wallet wallet);

    void updateBalance(int walletId, BigDecimal newBalance);
}
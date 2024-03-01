package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WalletRepository {

    List<Wallet> getAll();

   Optional< Wallet> getWalletById(int walletId);

    Optional<List<Wallet>> getByCreatorId(int creatorId);

    Wallet create(Wallet wallet);

    void update(Wallet wallet);

    void delete(Wallet wallet);

    void updateBalance(int walletId, BigDecimal newBalance);
}
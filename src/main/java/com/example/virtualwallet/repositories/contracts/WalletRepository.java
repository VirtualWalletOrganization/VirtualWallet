package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Wallet;

public interface WalletRepository {
    Wallet getWalletById(int walletId);
}

package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

public interface BankService {
    void transferMoneyOut(Transfer transferOut, Wallet senderWallet, User user);

    void transferMoneyIn(Transfer transferIn, Wallet receiverWallet, User user);
}

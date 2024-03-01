package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transfer;

import java.util.List;

public interface TransferService {


    List<Transfer> getAllTransfers();

    Transfer getTransferById(int transferId);

    Transfer createTransfer(Transfer transfer);

    void updateTransfer(Transfer transfer);

    void deleteTransfer(Transfer transfer);

    void transferMoney(int senderUserId, int recipientUserId, int walletId, double amount);

    void confirmTransfer(int transferId, int recipientId, int recipientWalletId);

    void editTransfer(int transferId, double newAmount);
}
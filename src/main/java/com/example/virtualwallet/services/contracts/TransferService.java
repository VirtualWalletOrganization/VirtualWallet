package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.enums.Status;

import java.math.BigDecimal;
import java.util.List;

public interface TransferService {

    List<Transfer> getAllTransfers();

    Transfer getTransferById(int transferId);

    List<Transfer> getAllTransfersByStatus(Status status);

    Transfer createTransfer(Transfer transfer);

    void updateTransfer(Transfer transfer);

    void deleteTransfer(Transfer transfer);

    void transferMoney(int senderUserId, int recipientUserId, int walletId, BigDecimal amount);

    void confirmTransfer(int transferId, int senderUserId, int recipientId, int recipientWalletId);

    void editTransfer(int transferId, BigDecimal newAmount);
}
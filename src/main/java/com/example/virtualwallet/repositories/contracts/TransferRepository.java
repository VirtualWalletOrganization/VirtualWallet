package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Transfer;

import java.util.List;

public interface TransferRepository {
    List<Transfer> getAllTransfers();

    Transfer getTransferById(int transferId);

    Transfer create(Transfer transfer);

    void update(Transfer transfer);

    void delete(Transfer transfer);
}

package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransferRepository {

    Optional<List<Transfer>> getAllTransfers();

    Optional<Transfer> getTransferById(int transferId);

    Transfer create(Transfer transfer);

    void update(Transfer transfer);

    void delete(Transfer transfer);
}

package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.enums.Status;

import java.util.List;
import java.util.Optional;

public interface TransferRepository {

    Optional<List<Transfer>> getAllTransfers();

    Optional<Transfer> getTransferById(int transferId);

    Optional<List<Transfer>> getAllTransfersByUserId(int userId);

    Optional<List<Transfer>> getAllTransfersByStatus(Status status);

    Transfer create(Transfer transfer);

    void update(Transfer transfer);

    void delete(Transfer transfer);
}

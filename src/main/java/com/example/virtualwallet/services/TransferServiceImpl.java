package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransferRepository;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final UserService userService;

    @Autowired
    public TransferServiceImpl(TransferRepository transferRepository, UserService userService) {
        this.transferRepository = transferRepository;
        this.userService = userService;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return transferRepository.getAllTransfers()
                .orElseThrow(() -> new EntityNotFoundException("Transfers"));
    }

    @Override
    public Transfer getTransferById(int transferId) {
        return transferRepository.getTransferById(transferId)
                .orElseThrow(() -> new EntityNotFoundException("Transfer", "id", String.valueOf(transferId)));
    }

    @Override
    public List<Transfer> getAllTransfersByUserId(int userId) {
        return transferRepository.getAllTransfersByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Transfers"));

    }

    @Override
    public List<Transfer> getAllTransfersByStatus(Status status) {
        return transferRepository.getAllTransfersByStatus(status)
                .orElseThrow(() -> new EntityNotFoundException("Transfers", "status", String.valueOf(status)));
    }

    @Override
    public Transfer createTransfer(Transfer transfer) {
        return transferRepository.create(transfer);
    }

    @Override
    public void updateTransfer(Transfer transfer) {
        transferRepository.update(transfer);
    }

    @Override
    public void deleteTransfer(Transfer transfer) {
        transferRepository.delete(transfer);
    }

    @Override
    public void transferMoney(int senderUserId, int recipientUserId, int walletId, BigDecimal amount) {
        User sender = userService.getById(senderUserId);
        User recipient = userService.getById(recipientUserId);
        Wallet wallet = sender.getWallets().stream()
                .filter(w -> w.getId() == walletId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in sender's wallet.");
        }

        Transfer transfer = new Transfer();
//        transfer.setSender(sender);
//        transfer.setRecipient(recipient);
        transfer.setAmount(amount);
        transfer.setCurrency(wallet.getCurrency());
//        transfer.setDirection(Direction.OUTGOING);
        transfer.setDate(Timestamp.valueOf(LocalDateTime.now()));
        transfer.setStatus(Status.PENDING);
        // transfer.setDescription("Transfer from " + sender.getUsername() + " to " + recipient.getUsername());
        transferRepository.create(transfer);

        wallet.setBalance(wallet.getBalance().subtract((amount)));
        userService.updateUser(sender, recipient);
    }

    @Override
    public void confirmTransfer(int transferId, int senderUserId, int recipientId, int recipientWalletId) {
        Transfer transfer = getTransferById(transferId);
        transfer.setStatus(Status.COMPLETED);
        transferRepository.update(transfer);

        User sender = userService.getById(senderUserId);
//        User recipient = transfer.getRecipient();
        User recipient = userService.getById(recipientId);
        Wallet wallet = recipient.getWallets().stream()
                .filter(w -> w.getId() == recipientWalletId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(recipientWalletId)));
        wallet.setBalance(wallet.getBalance().add((transfer.getAmount())));
        userService.updateUser(sender, recipient);
    }

    @Override
    public void editTransfer(int transferId, BigDecimal newAmount) {
        Transfer transfer = getTransferById(transferId);
        transfer.setAmount(newAmount);
        transferRepository.update(transfer);
    }
}
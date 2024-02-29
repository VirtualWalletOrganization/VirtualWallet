package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Direction;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransferRepository;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.services.contracts.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransferServiceImpl(TransferRepository transferRepository, UserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        return transferRepository.getAllTransfers();
    }

    @Override
    public Transfer getTransferById(int transferId) {
        return transferRepository.getTransferById(transferId);
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
    public void transferMoney(int senderUserId, int recipientUserId, int walletId, double amount) {
        User sender = userRepository.getById(senderUserId);
        User recipient = userRepository.getById(recipientUserId);
        Wallet wallet = sender.getWallets().stream()
                .filter(w -> w.getId() == walletId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));

        if (wallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in sender's wallet.");
        }

        Transfer transfer = new Transfer();
//        transfer.setSender(sender);
//        transfer.setRecipient(recipient);
        transfer.setAmount(amount);
        transfer.setCurrency(wallet.getCurrency());
        transfer.setDirection(Direction.OUTGOING);
        transfer.setDate(new Date());
        transfer.setStatus(Status.PENDING);
        transfer.setDescription("Transfer from " + sender.getUsername() + " to " + recipient.getUsername());
        transferRepository.create(transfer);

        wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
        userRepository.updateUser(sender);
    }

    @Override
    public void confirmTransfer(int transferId, int recipientId, int recipientWalletId) {
        Transfer transfer = transferRepository.getTransferById(transferId);
        transfer.setStatus(Status.COMPLETED);
        transferRepository.update(transfer);

//        User recipient = transfer.getRecipient();
        User recipient = userRepository.getById(recipientId);
        Wallet wallet = recipient.getWallets().stream()
                .filter(w -> w.getId() == recipientWalletId)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(recipientWalletId)));
        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(transfer.getAmount())));
        userRepository.updateUser(recipient);
    }

    @Override
    public void editTransfer(int transferId, double newAmount) {
        Transfer transfer = transferRepository.getTransferById(transferId);
        transfer.setAmount(newAmount);
        transferRepository.update(transfer);
    }
}
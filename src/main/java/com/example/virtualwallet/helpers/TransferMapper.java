package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransferRequestDto;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TransferMapper {

    private WalletService walletService;

    public TransferMapper(WalletService walletService) {
        this.walletService = walletService;
    }

    public Transfer fromDto(TransferRequestDto transferRequestDto, User user, Card card) {
        Transfer transfer = new Transfer();
        Wallet wallet = walletService.getWalletById(transferRequestDto.getReceiverWalletId(), user.getId());
        transfer.setReceiverWallet(wallet);
        transfer.setReceiver(user);
        transfer.setSender(user);
        transfer.setCard(card);
        transfer.setAmount(transferRequestDto.getAmount());
        transfer.setCurrency("USD");
        transfer.setDate(Timestamp.valueOf(LocalDateTime.now()));
        transfer.setStatus(Status.PENDING);
        transfer.setDescription(transferRequestDto.getDescription());

        return transfer;
    }
}
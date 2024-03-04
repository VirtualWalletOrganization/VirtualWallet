package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransferRequestDto;
import com.example.virtualwallet.models.enums.Direction;
import com.example.virtualwallet.models.enums.Status;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TransferMapper {

    public Transfer fromDtoMoneyOut(Wallet senderWallet, TransferRequestDto transferRequestDto,
                                    SpendingCategory spendingCategory) {
        Transfer transfer = new Transfer();
        transfer.setWallet(senderWallet);
        transfer.setAccountNumber(transferRequestDto.getRecipientAccount());
        transfer.setAmount(transferRequestDto.getAmount());
        transfer.setCurrency(transferRequestDto.getCurrency());
        transfer.setDirection(Direction.OUTGOING);
        transfer.setDate(new Date());
        transfer.setStatus(Status.PENDING);
        transfer.setSpendingCategory(spendingCategory);
        return transfer;
    }

    public Transfer fromDtoMoneyIn(Wallet receiverWallet, TransferRequestDto transferRequestDto,
                                   SpendingCategory spendingCategory) {
        Transfer transfer = fromDtoMoneyOut(receiverWallet, transferRequestDto, spendingCategory);
        transfer.setAccountNumber(transferRequestDto.getSenderAccount());
        transfer.setDirection(Direction.INCOMING);
        return transfer;
    }
}

package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.CardDto;
import com.example.virtualwallet.models.dtos.MockBankDto;
import com.example.virtualwallet.models.dtos.TransferRequestDto;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferMapper {

    private WalletService walletService;

    public TransferMapper(WalletService walletService) {
        this.walletService = walletService;
    }

//    public Transfer fromDtoMoneyOut(Wallet senderWallet, TransferRequestDto transferRequestDto) {
//        Transfer transfer = new Transfer();
//        transfer.setWallet(senderWallet);
//        transfer.setAccountNumber(transferRequestDto.getAccountNumber());
//        transfer.setAmount(transferRequestDto.getAmount());
//        transfer.setCurrency(transferRequestDto.getCurrency());
//        transfer.setDirection(Direction.OUTGOING);
//        transfer.setDate(Timestamp.valueOf(LocalDateTime.now()));
//        transfer.setStatus(Status.PENDING);
//        SpendingCategory spendingCategory = new SpendingCategory();
//        spendingCategory.setName(transferRequestDto.getSpendingCategory());
//        transfer.setSpendingCategory(spendingCategory);
//        return transfer;
//    }

//    public Transfer fromDtoMoneyIn(Wallet receiverWallet, TransferRequestDto transferRequestDto) {
//        Transfer transfer = fromDtoMoneyOut(receiverWallet, transferRequestDto);
//        transfer.setDirection(Direction.INCOMING);
//        return transfer;
//    }

    public MockBankDto toDto(CardDto cardDto, TransferRequestDto transferRequestDto) {
        MockBankDto mockBankDto = new MockBankDto();
        mockBankDto.setCardNumber(cardDto.getCardNumber());
        mockBankDto.setExpireDate(cardDto.getExpirationDate().toString());
        mockBankDto.setCvv(cardDto.getCheckNumber());
        mockBankDto.setValue(transferRequestDto.getAmount());
        mockBankDto.setWalletId(transferRequestDto.getReceiverWalletId());
        return mockBankDto;
    }

    public Transfer fromDtoMoney(TransferRequestDto transferRequestDto,
                                 Status status,
                                 int walletId,
                                 Card selectedCard,
                                 User user) {
        Transfer transfer = new Transfer();
        transfer.setSender(user);
        transfer.setCard(selectedCard);
        transfer.setReceiver(user);
        Wallet wallet = walletService.getWalletById(walletId, user.getId());
        transfer.setReceiverWallet(wallet);
        transfer.setAmount(transferRequestDto.getAmount());
        transfer.setDescription(transferRequestDto.getDescription());
        transfer.setStatus(status);
        transfer.setCurrency("USD");
        return transfer;
    }
}
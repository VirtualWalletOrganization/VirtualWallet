package com.example.virtualwallet.services;

import com.example.virtualwallet.config.ExternalApiUrlConfig;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.MockBankDto;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.services.contracts.PaymentManager;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.virtualwallet.helpers.ExternalQuery.createExternalTransferRequestQuery;

@Service
public class PaymentManagerImpl implements PaymentManager {

    private final WalletService walletService;
    private final TransferService transferService;
    private final ExternalApiUrlConfig externalApiUrlConfig;
    private final UserService userService;

    @Autowired
    public PaymentManagerImpl(WalletService walletService, TransferService transferService,
                              ExternalApiUrlConfig externalApiUrlConfig, UserService userService) {
        this.walletService = walletService;
        this.transferService = transferService;
        this.externalApiUrlConfig = externalApiUrlConfig;
        this.userService = userService;
    }

    @Override
    public void setCardPaymentIn(int receiverWalletId, Transfer transfer, User user) {
        Wallet receiverWallet = walletService.getWalletById(receiverWalletId, user.getId());

        transferService.createTransfer(transfer);

        if (transfer.getStatus() == Status.COMPLETED) {
            walletService.addMoneyFromCardToWallet(transfer, receiverWallet, user);
        }
    }

    @Override
//    @Scheduled(cron = "0 */1 * * * *")
    public void checkPendingReceivedTransactionStatuses() {
        List<Transfer> transfers = null;
        User bankAuthorization = userService.getById(Integer.parseInt(externalApiUrlConfig.getBankUserId()));

        try {
            transfers = transferService.getAllTransfersByStatus(Status.PENDING);
        } catch (EntityNotFoundException e) {
            return;
        }

        List<Transfer> transfersToUpdate = new ArrayList<>();
        MockBankDto dto = new MockBankDto();

        for (Transfer transfer : transfers) {
            dto.setWalletId(transfer.getReceiverWallet().getId());
            ResponseEntity<String> response;

            try {
                String checkTransactionStatusUrl = externalApiUrlConfig.getMockBankStatusCheckUrl();
                response = createExternalTransferRequestQuery(dto, checkTransactionStatusUrl);

                if (Objects.requireNonNull(response.getBody()).equalsIgnoreCase("completed")) {
                    transfer.setStatus(Status.COMPLETED);
                    transfersToUpdate.add(transfer);
                }
            } catch (HttpClientErrorException e) {
                throw e;
            }
        }
    }
}
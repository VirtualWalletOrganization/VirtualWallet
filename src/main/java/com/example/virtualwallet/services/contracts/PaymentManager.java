package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;

public interface PaymentManager {

    void setCardPaymentIn(int receiverWalletId, Transfer transfer, User user);

    void checkPendingReceivedTransactionStatuses();
}
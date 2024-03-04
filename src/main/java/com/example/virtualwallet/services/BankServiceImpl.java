package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.TransferRepository;
import com.example.virtualwallet.services.contracts.BankService;
import com.example.virtualwallet.services.contracts.SpendingCategoryService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

import static com.example.virtualwallet.utils.Messages.ERROR_INSUFFICIENT_BALANCE;

@Service
public class BankServiceImpl implements BankService {

    private final WalletService walletService;
    private final TransferRepository transferRepository;
    private final SpendingCategoryService spendingCategoryService;

    @Autowired
    public BankServiceImpl(WalletService walletService, TransferRepository transferRepository, SpendingCategoryService spendingCategoryService) {
        this.walletService = walletService;
        this.transferRepository = transferRepository;
        this.spendingCategoryService = spendingCategoryService;
    }

    public void transferMoneyOut(Transfer transferOut, Wallet senderWallet, User user) {
        SpendingCategory existingSpendingCategory = spendingCategoryService
                .getSpendingCategoryByName(transferOut.getSpendingCategory().getName());
        transferOut.setSpendingCategory(existingSpendingCategory);

        if (!isValidRequestTransferMoneyOut(transferOut, senderWallet)) {
            transferOut.setStatus(Status.FAILED);
            transferRepository.create(transferOut);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(transferOut.getAmount()));
        walletService.update(senderWallet, user);
        updateAccountBalanceTransferIn(transferOut.getAccountNumber(), transferOut.getAmount());
        transferOut.setStatus(Status.COMPLETED);
        transferRepository.create(transferOut);
    }

    public void transferMoneyIn(Transfer transferIn, Wallet receiverWallet, User user) {
        SpendingCategory existingSpendingCategory = spendingCategoryService
                .getSpendingCategoryByName(transferIn.getSpendingCategory().getName());
        transferIn.setSpendingCategory(existingSpendingCategory);

        if (!isValidRequestTransferMoneyIn(transferIn, receiverWallet)) {
            transferIn.setStatus(Status.FAILED);
            transferRepository.create(transferIn);
            throw new InsufficientBalanceException(ERROR_INSUFFICIENT_BALANCE);
        }

        receiverWallet.setBalance(receiverWallet.getBalance().add(transferIn.getAmount()));
        walletService.update(receiverWallet, user);
        updateAccountBalanceTransferOut(transferIn.getAccountNumber(), transferIn.getAmount());
        transferIn.setStatus(Status.COMPLETED);
        transferRepository.create(transferIn);
    }

    private boolean isValidRequestTransferMoneyOut(Transfer transferOut, Wallet senderWallet) {
        //transfer out wallet -
        BigDecimal balanceAfterTransfer = senderWallet.getBalance().subtract(transferOut.getAmount());
        return balanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;

        //return (senderWallet.getBalance().compareTo(senderWallet.getBalance().subtract(transferOut.getAmount())) >= 0);
//        //transfer in account +
//        if (updateAccountBalanceTransferIn(requestDto.getSenderAccount(), requestDto.getAmount())
//                .compareTo(updateAccountBalanceTransferIn(requestDto.getSenderAccount(), requestDto.getAmount())
//                        .add(requestDto.getAmount())) < 0) {
//        }
    }

    private boolean isValidRequestTransferMoneyIn(Transfer transferIn, Wallet senderWallet) {
        //transfer out account -

        BigDecimal balanceBeforeTransfer = updateAccountBalanceTransferOut(transferIn.getAccountNumber(),
                transferIn.getAmount());
        BigDecimal balanceAfterTransfer = balanceBeforeTransfer.subtract(transferIn.getAmount());
        return balanceAfterTransfer.compareTo(BigDecimal.ZERO) >= 0;
//        return (updateAccountBalanceTransferOut(transferIn.getAccountNumber(), transferIn.getAmount())
//                .compareTo(updateAccountBalanceTransferOut(transferIn.getAccountNumber(), transferIn.getAmount())
//                        .subtract(transferIn.getAmount())) >= 0);
        //        //transfer in wallet +
//        //Todo check after overdraft implementation
//        (senderWallet.getBalance().compareTo(senderWallet.getBalance().add(requestDto.getAmount())) < 0);
    }

    //BankAccounts
    private BigDecimal updateAccountBalanceTransferIn(String accountId, BigDecimal transferAmount) {
        return getAccountBalance(accountId).add(transferAmount);
    }

    private BigDecimal updateAccountBalanceTransferOut(String accountId, BigDecimal transferAmount) {
        return getAccountBalance(accountId).subtract(transferAmount);
    }

    private BigDecimal getAccountBalance(String accountId) {
        BigDecimal currentBalance = BigDecimal.valueOf(1000);
        Random random = new Random();
        int randomNumber = random.nextInt();
        BigDecimal randomBigDecimal = BigDecimal.valueOf(randomNumber);
        currentBalance = currentBalance.subtract(randomBigDecimal);
        return currentBalance;
    }
}
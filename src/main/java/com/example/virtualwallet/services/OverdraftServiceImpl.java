package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Overdraft;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.repositories.contracts.OverdraftRepository;
import com.example.virtualwallet.services.contracts.OverdraftService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OverdraftServiceImpl implements OverdraftService {

    private final OverdraftRepository overdraftRepository;
    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public OverdraftServiceImpl(OverdraftRepository overdraftRepository, WalletService walletService, UserService userService) {
        this.overdraftRepository = overdraftRepository;
        this.walletService = walletService;
        this.userService = userService;
    }

    @Override
    public List<Overdraft> getAllOverdrafts() {
        return overdraftRepository.getAllOverdrafts()
                .orElseThrow(() -> new EntityNotFoundException("Overdrafts"));
    }

    @Override
    public Overdraft getOverdraftById(int overdraftId) {
        return overdraftRepository.getOverdraftById(overdraftId)
                .orElseThrow(() -> new EntityNotFoundException("Overdraft", "id", String.valueOf(overdraftId)));
    }

    @Override
    public Overdraft create(Overdraft overdraft) {
        return overdraftRepository.create(overdraft);
    }

    @Override
    public void update(Overdraft overdraft) {
        overdraftRepository.update(overdraft);
    }

    @Override
    public void delete(Overdraft overdraft) {
        overdraftRepository.delete(overdraft);
    }

    @Override
    public void enableOverdraft(int userId, boolean enable) {
        List<Wallet> wallets = walletService.getByCreatorId(userId);
        for (Wallet wallet : wallets) {
            wallet.setOverdraftEnabled(enable);
            walletService.update(wallet);
        }
    }

    @Override
    public void chargeInterest() {
        List<Overdraft> overdrafts = getAllOverdrafts();

        for (Overdraft overdraft : overdrafts) {
            if (overdraft.isPaid()) {
                continue;
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate dueDate = LocalDate.parse(overdraft.getDueDate().toString());

            if (currentDate.isAfter(dueDate)) {
                BigDecimal balance = walletService.getWalletById(overdraft.getWallet().getId()).getBalance();
                BigDecimal interest = balance.multiply(BigDecimal.valueOf(overdraft.getOverdraftType().getInterestRate()));
                balance = balance.add(interest);
                walletService.updateBalance(overdraft.getWallet().getId(), balance);
                overdraft.setPaid(true);
                overdraftRepository.update(overdraft);
            }
        }
    }

    @Override
    public void blockAccounts(int userId) {
        User user = userService.getById(userId);
        user.setStatus(UserStatus.BLOCKED);
    }

    @Override
    public void updatePaidStatus(int overdraftId, boolean isPaid) {
        overdraftRepository.updatePaidStatus(overdraftId, isPaid);
    }
}
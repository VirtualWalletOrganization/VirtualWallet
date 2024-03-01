package com.example.virtualwallet.services;

import com.example.virtualwallet.models.Overdraft;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.repositories.contracts.OverdraftRepository;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.OverdraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class OverdraftServiceImpl implements OverdraftService {

    private final OverdraftRepository overdraftRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Autowired
    public OverdraftServiceImpl(OverdraftRepository overdraftRepository, WalletRepository walletRepository, UserRepository userRepository) {
        this.overdraftRepository = overdraftRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Overdraft> getAllOverdrafts() {
        return overdraftRepository.getAllOverdrafts();
    }

    @Override
    public Overdraft getOverdraftById(int overdraftId) {
        return overdraftRepository.getOverdraftById(overdraftId);
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
        List<Wallet> wallets = walletRepository.getByCreatorId(userId);
        for (Wallet wallet : wallets) {
            wallet.setOverdraftEnabled(enable);
            walletRepository.update(wallet);
        }
    }

    @Override
    public void chargeInterest() {
        List<Overdraft> overdrafts = overdraftRepository.getAllOverdrafts();

        for (Overdraft overdraft : overdrafts) {
            if (overdraft.isPaid()) {
                continue;
            }

            LocalDate currentDate = LocalDate.now();
            LocalDate dueDate = LocalDate.parse(overdraft.getDueDate().toString());

            if (currentDate.isAfter(dueDate)) {
                BigDecimal balance = walletRepository.getWalletById(overdraft.getWallet().getId()).getBalance();
                BigDecimal interest = balance.multiply(BigDecimal.valueOf(overdraft.getOverdraftType().getInterestRate()));
                balance = balance.add(interest);
                walletRepository.updateBalance(overdraft.getWallet().getId(), balance);
                overdraft.setPaid(true);
                overdraftRepository.update(overdraft);
            }
        }
    }

    @Override
    public void blockAccounts(int userId) {
        User user = userRepository.getById(userId);
        user.setStatus(UserStatus.BLOCKED);
    }

    @Override
    public void updatePaidStatus(int overdraftId, boolean isPaid) {
        overdraftRepository.updatePaidStatus(overdraftId, isPaid);
    }
}
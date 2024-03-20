package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Overdraft;

import java.util.List;

public interface OverdraftService {

    List<Overdraft> getAllOverdrafts();

    Overdraft getOverdraftById(int overdraftId);

    Overdraft create(Overdraft overdraft);

    void update(Overdraft overdraft);

    void delete(Overdraft overdraft);

    void enableOverdraft(int userId, boolean enable);

    void chargeInterest();

    void blockAccounts(int userId);

    void updatePaidStatus(int overdraftId, boolean isPaid);
}
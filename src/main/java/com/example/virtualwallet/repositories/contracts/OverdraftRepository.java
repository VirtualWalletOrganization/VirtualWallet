package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Overdraft;

import java.util.List;
import java.util.Optional;

public interface OverdraftRepository {

    Optional<List<Overdraft>> getAllOverdrafts();

    Optional<Overdraft> getOverdraftById(int overdraftId);

    Overdraft create(Overdraft overdraft);

    void update(Overdraft overdraft);

    void delete(Overdraft overdraft);

    void enableOverdraft(int userId, boolean enable);

    void updatePaidStatus(int overdraftId, boolean isPaid);
}
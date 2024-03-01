package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;

import java.util.Optional;

public interface ReferralRepository {

    Optional<Referral> getById(int id);

    Optional<String> getReferralEmail(String email);

    Optional<User> getReferrerUserIdByEmail(String email);

    Optional<Status> getReferralStatusByEmail(String email);

    Referral create(Referral referral);
}
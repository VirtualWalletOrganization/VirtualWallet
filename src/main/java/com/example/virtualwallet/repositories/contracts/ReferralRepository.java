package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;

import java.util.List;

public interface ReferralRepository {

    Referral findById(int id);

    String findReferralEmail(String email);

    User findReferrerUserIdByEmail(String email);

    Status findReferralStatusByEmail(String email);

    Referral create(Referral referral);
}
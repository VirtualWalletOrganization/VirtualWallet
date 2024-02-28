package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;

import java.util.List;

public interface ReferralService {

    Referral findById(int id);

    User findReferrerUserIdByEmail(String email);

    Status findReferralStatusByEmail(String email);

    Referral create(Referral referral);

    void referFriend(User user, String friendEmail);
}
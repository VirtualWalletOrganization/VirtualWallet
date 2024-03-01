package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;

public interface ReferralService {

    Referral getById(int id);

    String getReferralEmail(String email);

    User getReferrerUserIdByEmail(String email);

    Status getReferralStatusByEmail(String email);

    Referral create(Referral referral);

    void referFriend(User user, String friendEmail);
}
package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.ReferralRepository;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.services.contracts.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReferralServiceImpl(ReferralRepository referralRepository, UserRepository userRepository) {
        this.referralRepository = referralRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Referral getById(int id) {
        return referralRepository.getById(id);
    }

    @Override
    public User getReferrerUserIdByEmail(String email) {
        return referralRepository.getReferrerUserIdByEmail(email);
    }

    @Override
    public Status getReferralStatusByEmail(String email) {
        return referralRepository.getReferralStatusByEmail(email);
    }

    @Override
    public Referral create(Referral referral) {
        return referralRepository.create(referral);
    }

    @Override
    public void referFriend(User user, String friendEmail) {
        User existingUser = userRepository.getByEmail(user.getEmail());

        if (existingUser != null) {
            throw new DuplicateEntityException("User", "email", friendEmail);
        }

        String email = referralRepository.getReferralEmail(friendEmail);

        if (email == null) {
            //send referral email and set scheduled for 7 days.
            // If email expired we need to change status to expired

            Referral referral = new Referral();
            referral.setUser(user);
            referral.setReferredEmail(friendEmail);
            referral.setReferralStatus(Status.PENDING);
            referralRepository.create(referral);
        }
    }
}
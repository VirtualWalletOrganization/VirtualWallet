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
    public Referral findById(int id) {
        return referralRepository.findById(id);
    }

    @Override
    public User findReferrerUserIdByEmail(String email) {
        return referralRepository.findReferrerUserIdByEmail(email);
    }

    @Override
    public Status findReferralStatusByEmail(String email) {
        return referralRepository.findReferralStatusByEmail(email);
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

        String email = referralRepository.findReferralEmail(friendEmail);

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
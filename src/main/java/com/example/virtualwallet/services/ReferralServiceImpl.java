package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.ReferralRepository;
import com.example.virtualwallet.services.contracts.ReferralService;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final UserService userService;

    @Autowired
    public ReferralServiceImpl(ReferralRepository referralRepository, UserService userService) {
        this.referralRepository = referralRepository;
        this.userService = userService;
    }

    @Override
    public Referral getById(int id) {
        return referralRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Referral", "id", String.valueOf(id)));
    }

    @Override
    public String getReferralEmail(String email) {
        return referralRepository.getReferralEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Referral", "email", email));
    }

    @Override
    public User getReferrerUserIdByEmail(String email) {
        return referralRepository.getReferrerUserIdByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public Status getReferralStatusByEmail(String email) {
        return referralRepository.getReferralStatusByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Status", "email", email));
    }

    @Override
    public Referral create(Referral referral) {
        return referralRepository.create(referral);
    }

    @Override
    public void referFriend(User user, String friendEmail) {
        User existingUser = userService.getByEmail(user.getEmail());

        if (existingUser != null) {
            throw new DuplicateEntityException("User", "email", friendEmail);
        }

        Optional<String> email = referralRepository.getReferralEmail(friendEmail);

        if (email.isEmpty()) {
            Referral referral = new Referral();
            referral.setUser(user);
            referral.setReferredEmail(friendEmail);
            referral.setReferralStatus(Status.PENDING);
            referralRepository.create(referral);
        }
    }
}
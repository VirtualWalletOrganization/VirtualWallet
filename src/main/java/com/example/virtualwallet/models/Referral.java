package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "referrer_user_id")
    private User referrerUser;

    @Column(name = "referred_email")
    private String referredEmail;

    @Column(name = "referral_code")
    private String referralCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "referral_status")
    private Status referralStatus;

    public Referral() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getReferrerUser() {
        return referrerUser;
    }

    public void setReferrerUser(User referrerUser) {
        this.referrerUser = referrerUser;
    }

    public String getReferredEmail() {
        return referredEmail;
    }

    public void setReferredEmail(String referredEmail) {
        this.referredEmail = referredEmail;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Status getReferralStatus() {
        return referralStatus;
    }

    public void setReferralStatus(Status referralStatus) {
        this.referralStatus = referralStatus;
    }
}
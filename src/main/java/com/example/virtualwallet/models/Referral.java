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
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "referred_email")
    private String referredEmail;

    @Column(name = "referral_code")
    private String referralCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "referral_status")
    private Status referralStatus;

    @Column(name = "bonus")
    private double bonus;

    public Referral() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
}
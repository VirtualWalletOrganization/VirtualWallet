package com.example.virtualwallet.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "referrals")
public class Referral {
    @Id
    @Column(name = "referral_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "referrer_user_id")
    @JsonIgnoreProperties({"id", "firstName", "lastName", "email", "role", "status", "isDeleted", "password", "profilePicture", "selfie", "photoCardId", "emailVerified", "overdraftEnabled", "overdraftLimit", "identity", "phoneNumber"})
    private User user;

    @Column(name = "referred_email")
    private String email;

    @Column(name = "referral_code")
    private String code;

    @Column(name = "referral_status")
    private ReferralStatus referralStatus;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ReferralStatus getReferralStatus() {
        return referralStatus;
    }

    public void setReferralStatus(ReferralStatus referralStatus) {
        this.referralStatus = referralStatus;
    }
}

package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Identity;
import jakarta.persistence.*;

@Entity
@Table(name = "identity_statuses")
public class IdentityStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identity_status_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_verification_name")
    private Identity identity;

    public IdentityStatus() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
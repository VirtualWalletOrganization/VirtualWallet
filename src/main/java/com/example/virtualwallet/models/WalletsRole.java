package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.WalletRole;
import jakarta.persistence.*;

@Entity
@Table(name = "wallets_roles")
public class WalletsRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_role_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_role_name")
    private WalletRole walletRole;

    public WalletsRole() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WalletRole getWalletRole() {
        return walletRole;
    }

    public void setWalletRole(WalletRole walletRole) {
        this.walletRole = walletRole;
    }
}
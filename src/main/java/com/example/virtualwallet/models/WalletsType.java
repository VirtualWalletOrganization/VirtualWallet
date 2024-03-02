package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.WalletType;
import jakarta.persistence.*;
@Entity
@Table(name = "wallets_types")
public class WalletsType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_type_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type_name")
    private WalletType walletType;

    public WalletsType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = WalletType.valueOf(walletType.name());
    }
}
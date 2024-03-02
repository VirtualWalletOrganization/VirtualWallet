package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.WalletRole;
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

}

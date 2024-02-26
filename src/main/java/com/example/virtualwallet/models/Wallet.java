package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.WalletType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @Column(name = "wallet_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "balance")
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type")
    private WalletType walletType;

    @Column(name = "currency")
    private String currency;

    @Column(name = "is_default")
    private Boolean isDefault = Boolean.FALSE;
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role = Role.USER;

    @JsonIgnore
    @Column(name = "is_deleted")
    private Boolean isDeleted = Boolean.FALSE;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cards_wallets",
            joinColumns = @JoinColumn(name = "wallet_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cards;

    @JsonIgnore
    @ManyToMany(mappedBy = "wallets")
    private Set<User> users;

    public Wallet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Set<Card> getCards() {
        return cards;
    }

    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}

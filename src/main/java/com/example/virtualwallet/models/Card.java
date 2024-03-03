package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.CardStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "card_type_id")
    private CardsType cardsType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "card_holder")
    private String cardHolder;

    @Column(name = "check_number")
    private String checkNumber;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CardStatus cardStatus = CardStatus.ACTIVE;

//    @JsonIgnore
//    @ManyToMany(mappedBy = "cards",fetch = FetchType.EAGER)
//    private Set<Wallet> wallets;


    public Card() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CardsType getCardsType() {
        return cardsType;
    }

    public void setCardsType(CardsType cardsType) {
        this.cardsType = cardsType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

//    public Set<Wallet> getWallets() {
//        return new HashSet<>();
//    }
//
//    public void setWallets(Set<Wallet> wallets) {
//        this.wallets = wallets;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && cardsType == card.cardsType && Objects.equals(cardNumber, card.cardNumber)
                && Objects.equals(expirationDate, card.expirationDate) && Objects.equals(cardHolder, card.cardHolder)
                && Objects.equals(checkNumber, card.checkNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardsType, cardNumber, expirationDate, cardHolder, checkNumber);
    }


}
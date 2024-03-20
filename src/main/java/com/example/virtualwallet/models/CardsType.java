package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.CardType;
import jakarta.persistence.*;

@Entity
@Table(name = "cards_types")
public class CardsType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_type_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type_name")
    private CardType cardType;

    public CardsType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
}
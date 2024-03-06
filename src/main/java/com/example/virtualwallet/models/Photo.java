package com.example.virtualwallet.models;

import jakarta.persistence.*;

@Entity
@Table(name = "photos_verifications")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private int id;

    @OneToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "photo_card_id")
    private String cardId;

    @Column(name = "selfie")
    private String selfie;

    public Photo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getSelfie() {
        return selfie;
    }

    public void setSelfie(String selfie) {
        this.selfie = selfie;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
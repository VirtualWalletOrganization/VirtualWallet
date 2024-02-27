package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;

import java.util.List;

public interface CardRepository {

    List<Card> getAllCardsByUserId(int userId);

    Card getCardById(int cardId);

    void addCard(Card card);

    void updateCard(Card card);

    void deleteCard(Card card);
}
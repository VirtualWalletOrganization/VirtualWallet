package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;

import java.util.List;

public interface CardService {

    List<Card> getAllCardsByUserId(int userId);

    Card getCardById(int cardId);

    void addCard(Card card, int walletId, User user);

    void updateCard(Card card, User user);

    void deleteCard(int cardId, User user);
}
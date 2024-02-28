package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;

import java.util.Date;
import java.util.List;

public interface CardRepository {

    List<Card> getAllCards();

    List<Card> getAllCardsByUserId(int userId);

    Card getCardById(int cardId);

    Card getByCardNumber(String cardNumber);

    void addCard(Card card);

    void updateCard(Card card);

    void deleteCard(Card card);

    List<Card> findExpiredCards(Date currentDate);
}
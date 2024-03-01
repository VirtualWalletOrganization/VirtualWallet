package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Card;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CardRepository {

    List<Card> getAllCards();

    Optional<List<Card>> getAllCardsByUserId(int userId);

    Optional<Card> getCardById(int cardId);

    Optional<Card> getByCardNumber(String cardNumber);

    void addCard(Card card);

    void updateCard(Card card);

    void deleteCard(Card card);

    List<Card> findExpiredCards(Date currentDate);
}
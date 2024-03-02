package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CardRepository {

    List<Card> getAllCards();

    Optional<List<Card>> getAllCardsByUserId(int userId);

    Optional<Card> getCardById(int cardId);

    Optional<User> existsUserWithCard(int cardId, int userId);

    Optional<Card> getByCardNumber(String cardNumber);

    Card addCard(Card card);

    void updateCard(Card card);

    void deleteCard(Card card);

    List<Card> findExpiredCards(Date currentDate);
}
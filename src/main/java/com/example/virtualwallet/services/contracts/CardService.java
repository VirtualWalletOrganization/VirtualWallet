package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;

import java.util.List;

public interface CardService {

    List<Card> getAllCards();

    List<Card> getAllCardsByCurrentUser( User user);

    Card getCardById(int cardId, User user);

    Card getCardByCardNumber(String cardNumber);

    void addCard(Card card, int walletId, User user);

    void updateCard(Card card, User user);

    void deleteCard(int cardId, User user);

    void deactivateExpiredCards();
}
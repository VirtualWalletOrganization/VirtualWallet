package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.CardsType;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.CardDto;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card fromDto(int id, CardDto dto, User user) {
        Card card = fromDto(dto, user);
        card.setId(id);

        return card;
    }

    public Card fromDto(CardDto dto, User user) {
        Card card = new Card();

        CardsType cardsType = new CardsType();
        cardsType.setId(dto.getCardType().ordinal() + 1);
        cardsType.setCardType(dto.getCardType());
        card.setCardsType(cardsType);

        card.setCardNumber(dto.getCardNumber());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCardHolder(dto.getCardHolder());
        card.setCheckNumber(dto.getCheckNumber());
        card.setCurrency(dto.getCurrency());
        card.setUser(user);

        return card;
    }

    public CardDto toDto(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setCardType(card.getCardsType().getCardType());
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setCardHolder(card.getCardHolder());
        cardDto.setCheckNumber(card.getCheckNumber());
        cardDto.setExpirationDate(card.getExpirationDate());
        cardDto.setCurrency(card.getCurrency());

        return cardDto;
    }

    public CardDto toDtoCard(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setCardNumber(card.getCardNumber());
        cardDto.setExpirationDate(card.getExpirationDate());
        cardDto.setCardHolder(card.getCardHolder());
        cardDto.setCheckNumber(card.getCheckNumber());
        cardDto.setCardType(card.getCardsType().getCardType());

        return cardDto;
    }
}
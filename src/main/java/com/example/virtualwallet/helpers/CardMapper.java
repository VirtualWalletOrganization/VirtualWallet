package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.dtos.CardDto;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card fromDto(CardDto dto) {
        Card card = new Card();
        card.getCardsType().setCardType(dto.getCardType());
        card.setCardNumber(dto.getCardNumber());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCardHolder(dto.getCardHolder());
        card.setCheckNumber(dto.getCheckNumber());
        card.setCurrency(dto.getCurrency());
        return card;

    }

    public Card fromDto(int id, CardDto dto) {
        Card card = fromDto(dto);
        card.setId(id);


        return card;
    }
}

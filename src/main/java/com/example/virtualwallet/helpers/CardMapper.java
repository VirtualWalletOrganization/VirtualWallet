package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.CardsType;
import com.example.virtualwallet.models.dtos.CardDto;
import com.example.virtualwallet.models.enums.CardType;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card fromDto(int id, CardDto dto) {
        Card card = fromDto(dto);
        card.setId(id);

        return card;
    }

    public Card fromDto(CardDto dto) {
        Card card = new Card();
        CardsType cardsType=new CardsType();
        cardsType.setCardType(CardType.valueOf(dto.getCardType().name()));
        cardsType.setId(dto.getCardType().ordinal()+1);

        card.setCardsType(cardsType);
        card.setCardNumber(dto.getCardNumber());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCardHolder(dto.getCardHolder());
        card.setCheckNumber(dto.getCheckNumber());
        card.setCurrency(dto.getCurrency());

        return card;

    }
}

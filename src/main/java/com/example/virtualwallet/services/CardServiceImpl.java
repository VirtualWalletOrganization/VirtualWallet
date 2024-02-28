package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.CardStatus;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.virtualwallet.utils.CheckPermissions.checkAccessPermissionsUser;
import static com.example.virtualwallet.utils.Messages.MODIFY_CARD_ERROR_MESSAGE;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, WalletRepository walletRepository) {
        this.cardRepository = cardRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public List<Card> getAllCardsByUserId(int userId) {
        return cardRepository.getAllCardsByUserId(userId);
    }

    @Override
    public Card getCardById(int cardId) {
        return cardRepository.getCardById(cardId);
    }

    @Override
    public void addCard(Card card, int walletId, User user) {
        Wallet wallet = walletRepository.getWalletById(walletId);

        if (wallet.getCards()
                .stream()
                .anyMatch(c -> c.getCardNumber().equals(card.getCardNumber()))) {
            throw new DuplicateEntityException("Card", "card holder", card.getCardHolder());
        }

        wallet.getCards().add(card);
        card.setUser(user);
        card.getWallets().add(wallet);
        cardRepository.addCard(card);
    }

    @Override
    public void updateCard(Card card, User user) {
        Card cardToUpdate = cardRepository.getCardById(card.getId());
        checkAccessPermissionsUser(cardToUpdate.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        cardRepository.updateCard(card);
    }

    @Override
    public void deleteCard(int cardId, User user) {
        Card cardToDelete = cardRepository.getCardById(cardId);
        List<Wallet> wallets = new ArrayList<>(cardToDelete.getWallets());

        for (Wallet wallet : wallets) {
            wallet.getCards().remove(cardToDelete);
        }

        checkAccessPermissionsUser(cardToDelete.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        cardRepository.deleteCard(cardToDelete);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void deactivateExpiredCards() {
        Date currentDate = new Date();
        List<Card> expiredCards = cardRepository.findExpiredCards(currentDate);

        for (Card card : expiredCards) {
            card.setCardStatus(CardStatus.DEACTIVATED);
            cardRepository.updateCard(card);
        }
    }
}
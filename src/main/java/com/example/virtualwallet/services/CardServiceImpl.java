package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.CardStatus;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.checkAccessPermissionsUser;
import static com.example.virtualwallet.utils.CheckPermissions.checkBlockOrDeleteUser;
import static com.example.virtualwallet.utils.Messages.MODIFY_CARD_ERROR_MESSAGE;
import static com.example.virtualwallet.utils.Messages.USER_HAS_BEEN_BLOCKED_OR_DELETED;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final WalletService walletService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, WalletService walletService) {
        this.cardRepository = cardRepository;
        this.walletService = walletService;
    }

    @Override
    public List<Card> getAllCards() {
        return cardRepository.getAllCards();
    }

    @Override
    public List<Card> getAllCardsByUserId(int userId) {
        return cardRepository.getAllCardsByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cards"));
    }

    @Override
    public Card getCardById(int cardId) {
        return cardRepository.getCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card", "id", String.valueOf(cardId)));
    }

    @Override
    public Card getCardByCardNumber(String cardNumber) {
        return cardRepository.getByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card", "card number", String.valueOf(cardNumber)));
    }

    @Override
    public void addCard(Card card, int walletId, User user) {
        throwIfCardWithSameNumberAlreadyExistsInSystem(card);
        Wallet wallet = walletService.getWalletById(walletId);
        throwIfCardWithSameNumberAlreadyExistsInWallet(card, wallet);
        wallet.getCards().add(card);
        card.setUser(user);
        card.getWallets().add(wallet);
        cardRepository.addCard(card);
    }


    @Override
    public void updateCard(Card card, User user) {
        Card cardToUpdate = getCardById(card.getId());
        checkAccessPermissionsUser(cardToUpdate.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        checkBlockOrDeleteUser(user, USER_HAS_BEEN_BLOCKED_OR_DELETED);
        throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(card);

        cardRepository.updateCard(card);
    }

    @Override
    public void deleteCard(int cardId, User user) {
        Card cardToDelete = getCardById(cardId);
        checkAccessPermissionsUser(cardToDelete.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        checkBlockOrDeleteUser(user, USER_HAS_BEEN_BLOCKED_OR_DELETED);
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

    private void throwIfCardWithSameNumberAlreadyExistsInSystem(Card card) {
        if (cardRepository.getByCardNumber(card.getCardNumber()).isPresent()) {
            throw new DuplicateEntityException("Card", "card number", card.getCardNumber());
        }
    }

    private void throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(Card card) {
        Optional<Card> existingCardOptional = getAllCards().stream()
                .filter(c -> c.getCardNumber().equals(card.getCardNumber()))
                .findFirst();
        existingCardOptional.ifPresent(existingCard -> {
            if (existingCard.getId() != card.getId()) {
                throw new DuplicateEntityException("Card", "card number", card.getCardNumber());}
        });

    }


    private void throwIfCardWithSameNumberAlreadyExistsInWallet(Card card, Wallet wallet) {
        if (wallet.getCards()
                .stream()
                .anyMatch(c -> c.getCardNumber().equals(card.getCardNumber()))) {
            throw new DuplicateEntityException("Card", "card number", String.valueOf(card.getCardNumber()), "has already exist in wallet");
        }
    }
}

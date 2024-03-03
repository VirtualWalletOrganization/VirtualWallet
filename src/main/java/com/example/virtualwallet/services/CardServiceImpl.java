package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.CardMismatchException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.CardStatus;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.checkAccessPermissionsUser;
import static com.example.virtualwallet.utils.Messages.*;

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
    public Card getCardById(int cardId, User executingUser) {
        cardRepository.existsUserWithCard(cardId, executingUser.getId())
                .orElseThrow(() -> new AuthorizationException(SEARCH_CARD_ERROR_MESSAGE));
        return cardRepository.getCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card", "id", String.valueOf(cardId)));
    }

    @Override
    public List<Card> getAllCardsByUserId(int userId, User executingUser) {
        checkAccessPermissionsUser(userId, executingUser, SEARCH_CARD_ERROR_MESSAGE);
        return cardRepository.getAllCardsByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cards"));
    }

    @Override
    public Card getCardByCardNumber(String cardNumber) {
        return cardRepository.getByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card", "card number", String.valueOf(cardNumber)));
    }

    @Override
    public Card addCard(Card card, int walletId, User user) {
        throwIfCardWithSameNumberAlreadyExistsInSystem(card);
        Wallet wallet = walletService.getWalletById(walletId, user.getId());
        checkPermissionToAddCard(walletId, user);

        // checkAccessPermissionsUser(wallet.getCreator().getId(), user, ADD_CARD_ERROR_MESSAGE);
        throwIfCardWithSameNumberAlreadyExistsInWallet(card, wallet);
        validateCard(card, user);

        Card cardToAdd = cardRepository.addCard(card);
        wallet.getCards().add(card);
        walletService.update(wallet, user);
        return cardToAdd;
    }


    @Override
    public void updateCard(Card cardToUpdate, User user) {
        throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(cardToUpdate);
        getCardById(cardToUpdate.getId(), user);
        checkAccessPermissionsUser(cardToUpdate.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        Wallet wallet = walletService.getWalletByCardId(cardToUpdate.getId(), user.getId());
        validateCard(cardToUpdate, user);
        updateCardInWallet(cardToUpdate, user, wallet);
        cardRepository.updateCard(cardToUpdate);
    }

    @Override
    public void deleteCard(int cardId, User user) {
        Card cardToDelete = getCardById(cardId, user);
        checkAccessPermissionsUser(cardToDelete.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        Wallet wallet = walletService.getWalletByCardId(cardToDelete.getId(), user.getId());
        wallet.getCards().removeIf(c -> c.getId() == cardToDelete.getId());
        walletService.update(wallet, user);
        cardRepository.deleteCard(cardToDelete);
    }

    @Override
    @Scheduled(cron = "0 0 0 1 * *")
    public void deactivateExpiredCards() {
        Date currentDate = new Date();
        List<Card> expiredCards = cardRepository.findExpiredCards(currentDate);
        expiredCards.forEach(card -> {
            card.setCardStatus(CardStatus.DEACTIVATED);
            cardRepository.updateCard(card);
        });
    }

    private void checkPermissionToAddCard(int walletId, User user) {
        List<User> allUsersInWallet=walletService.getAllUsersByWalletId(walletId, user.getId());
        allUsersInWallet.stream()
                .filter(u->u.getId()== user.getId())
                .findFirst().orElseThrow(()->new AuthorizationException(ADD_CARD_ERROR_MESSAGE));
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
                throw new DuplicateEntityException("Card", "card number", card.getCardNumber());
            }
        });
    }

    private static void validateCard(Card card, User user) {
        throwIfCardDetailsMismatch(card, user);
        throwIfCardExpired(card);
    }

    private void throwIfCardWithSameNumberAlreadyExistsInWallet(Card card, Wallet wallet) {
        if (wallet.getCards()
                .stream()
                .anyMatch(c -> c.getCardNumber().equals(card.getCardNumber()))) {
            throw new DuplicateEntityException("Card", "card number", String.valueOf(card.getCardNumber()), "has already exist in wallet");
        }
    }

    private static void throwIfCardDetailsMismatch(Card card, User user) {
        if (!(user.getFirstName() + " " + user.getLastName()).equals(card.getCardHolder())) {
            throw new CardMismatchException(CARD_MISMATCH_ERROR);
        }
        if (card.getCardNumber().length() != 16 || card.getCheckNumber().length() != 3) {
            throw new CardMismatchException(INVALID_CARD);
        }
    }

    private static void throwIfCardExpired(Card card) {
        if (card.getExpirationDate().before(new Date())||card.getCardStatus().equals(CardStatus.DEACTIVATED)) {
            throw new CardMismatchException(CARD_IS_EXPIRED_OR_DEACTIVATED);
        }
    }

    private void updateCardInWallet(Card cardToUpdate, User user, Wallet wallet) {
        for (Card cardInWallet : wallet.getCards()) {
            if (cardInWallet.getId() == cardToUpdate.getId()) {
                cardToUpdate.setCardsType(cardToUpdate.getCardsType());
                cardToUpdate.setUser(cardToUpdate.getUser());
                cardToUpdate.setCardNumber(cardToUpdate.getCardNumber());
                cardToUpdate.setExpirationDate(cardToUpdate.getExpirationDate());
                cardToUpdate.setCardHolder(cardToUpdate.getCardHolder());
                cardToUpdate.setCheckNumber(cardToUpdate.getCheckNumber());
                cardToUpdate.setCurrency(cardToUpdate.getCurrency());
                cardToUpdate.setCardStatus(cardToUpdate.getCardStatus());
            }
        }

        walletService.update(wallet, user);
    }
}
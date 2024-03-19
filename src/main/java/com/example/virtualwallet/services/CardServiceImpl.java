package com.example.virtualwallet.services;

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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.*;
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
    public List<Card> getAllCardsByCurrentUser(User executingUser) {
        List<Card> cards = cardRepository.getAllCardsByCurrentUser(executingUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cards"));
        checkPermissionShowingCardsByUser(cards, executingUser, SEARCH_CARD_ERROR_MESSAGE);
        return cards;

    }

    @Override
    public Card getCardById(int cardId, User executingUser) {
        Card card = cardRepository.getCardById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card", "id", String.valueOf(cardId)));
        checkAccessPermissionsUser(card.getUser().getId(), executingUser, SEARCH_CARD_ERROR_MESSAGE);
        return card;
    }

    @Override
    public Card getCardByCardNumber(String cardNumber) {
        return cardRepository.getByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Card", "card number", String.valueOf(cardNumber)));
    }

    @Override
    public void addCard(Card card, int walletId, User user) {
        throwIfCardWithSameNumberAlreadyExistsInSystem(card, user);
        Wallet wallet = walletService.getWalletById(walletId, user.getId());
        checkPermissionExistingUsersInWallet(wallet, user, ADD_CARD_ERROR_MESSAGE);

        // checkAccessPermissionsUser(wallet.getCreator().getId(), user, ADD_CARD_ERROR_MESSAGE);
        throwIfCardWithSameNumberAlreadyExistsInWallet(card, wallet);
        validateCard(card, user);
        Optional<Card> existingCard = cardRepository.getByCardNumber(card.getCardNumber());
        if (existingCard.isEmpty()) {
            cardRepository.addCard(card);
            wallet.getCards().add(card);
            walletService.update(wallet, user);
        } else {
            wallet.getCards().add(existingCard.get());
            walletService.update(wallet, user);
        }

    }


    @Override
    public void updateCard(Card cardToUpdate, User user) {
        throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(cardToUpdate, user);
        getCardById(cardToUpdate.getId(), user);
        checkAccessPermissionsUser(cardToUpdate.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
        List<Wallet> wallets = walletService.getWalletsByCardId(cardToUpdate.getId(), user.getId());
        validateCard(cardToUpdate, user);
        updateCardInWallets(cardToUpdate, user, wallets);
        cardRepository.updateCard(cardToUpdate);
    }

    @Override
    public void deleteCard(int cardId, User user) {
        Card cardToDelete = getCardById(cardId, user);
        checkAccessPermissionsUser(cardToDelete.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
//        wallet.getCards().removeIf(card -> card.getId()==cardToDelete.getId());
//        walletService.update(wallet,user);
        List<Wallet> wallets = walletService.getWalletsByCardId(cardToDelete.getId(), user.getId());
        wallets.forEach(wallet -> wallet.getCards().removeIf(c -> c.getId() == cardToDelete.getId()));
        wallets.forEach(wallet -> walletService.update(wallet, user));
        cardRepository.deleteCard(cardToDelete);
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void deactivateExpiredCards() {
        Date currentDate = new Date();
        List<Card> expiredCards = cardRepository.findExpiredCards(currentDate);

        expiredCards.forEach(card -> {
            card.setCardStatus(CardStatus.DEACTIVATED);
            cardRepository.updateCard(card);
        });
    }

    private void throwIfCardWithSameNumberAlreadyExistsInSystem(Card card, User user) {
        if (cardRepository.getByCardNumber(card.getCardNumber()).isPresent()) {
            for (Card existingCard : user.getCards()) {
                if (existingCard.getCardNumber().equals(card.getCardNumber()) && !existingCard.getUser().equals(user)) {
                    throw new DuplicateEntityException("Card", "card number", card.getCardNumber());
                }
            }
        }
    }

    private void throwIfAnotherCardWithSameNumberAlreadyExistsInSystem(Card card, User user) {
        Optional<Card> existingCardOptional = getAllCards().stream()
                .filter(c -> c.getCardNumber().equals(card.getCardNumber()))
                .findFirst();

        existingCardOptional.ifPresent(existingCard -> {
            if (existingCard.getId() != card.getId() && existingCard.getUser() != user) {
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
        if (card.getExpirationDate().isBefore(LocalDate.now()) || card.getCardStatus().equals(CardStatus.DEACTIVATED)) {
            throw new CardMismatchException(CARD_IS_EXPIRED_OR_DEACTIVATED);
        }
    }

    private void updateCardInWallets(Card cardToUpdate, User user, List<Wallet> wallets) {
        for (Wallet wallet : wallets) {
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
                walletService.update(wallet, user);
            }
        }
    }
}
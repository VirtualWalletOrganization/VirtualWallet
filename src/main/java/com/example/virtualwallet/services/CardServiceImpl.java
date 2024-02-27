package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.CardService;

import java.util.ArrayList;
import java.util.List;

import static com.example.virtualwallet.utils.CheckPermissions.checkAccessPermissionsUser;
import static com.example.virtualwallet.utils.Messages.MODIFY_CARD_ERROR_MESSAGE;

public class CardServiceImpl implements CardService {
     private final CardRepository cardRepository;
     private  final WalletRepository walletRepository;

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
        try{
            Wallet wallet = walletRepository.getWalletById(walletId);
            if(wallet.getCards().stream().anyMatch(c->c.getCardNumber().equals(card.getCardNumber()))){
                throw new DuplicateEntityException("Wallet","card id", String.valueOf(card.getId()));
            }

            wallet.setCreator(user);
            wallet.getCards().add(card);
            card.getWallets().add(wallet);
            cardRepository.addCard(card);

        }catch (DuplicateEntityException e){
            throw new DuplicateEntityException("Card","card holder", card.getCardHolder());


        }


    }

    @Override
    public void updateCard(Card card, User user) {
//        Card cardToUpdate = cardRepository.getCardById(card.getId());
//        Wallet wallet = walletRepository.getWalletById(walletId);
//        if(wallet.getCards().contains(card)){
//            throw new DuplicateEntityException("Card","id",String.valueOf(card.getId()));
//        }
//        checkAccessPermissionsUser(card.getUser().getId(), user, MODIFY_CARD_ERROR_MESSAGE);
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
}

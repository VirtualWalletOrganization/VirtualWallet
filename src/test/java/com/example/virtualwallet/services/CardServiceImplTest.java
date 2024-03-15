package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.repositories.contracts.CardRepository;
import com.example.virtualwallet.services.contracts.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.helpers.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {
    @Mock
    private CardRepository mockRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAllCards_Should_ReturnListOfCards() {
        Card card = createMockCard();
        // Arrange
        List<Card> expectedCards = Collections.singletonList(card);
        Mockito.when(mockRepository.getAllCards()).thenReturn((List<Card>) card);

        // Act
        List<Card> actualCards = cardService.getAllCards();

        // Assert
        assertEquals(expectedCards, actualCards);
    }

    @Test
    public void getCardById_Should_ReturnCard_When_MatchExists() {
        Card mockCard = new Card();
        User executingUser = new User();
        Mockito.when(mockRepository.existsUserWithCard(anyInt(), anyInt())).thenReturn(Optional.of(executingUser));
        Mockito.when(mockRepository.getCardById(anyInt())).thenReturn(Optional.of(mockCard));

        // Act
        Card result = cardService.getCardById(1, executingUser);

        // Assert
        assertNotNull(result);
        assertEquals(mockCard, result);
    }

    @Test
    public void getCardById_Should_ThrowEntityNotFoundException_When_NoMatchExists() {
        // Arrange
        User executingUser = new User();
        Mockito.when(mockRepository.existsUserWithCard(anyInt(), anyInt())).thenReturn(Optional.of(executingUser));

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> cardService.getCardById(1, executingUser));
    }

    @Test
    public void getAllCardsByUserId_Should_ReturnListOfCards() {
        // Arrange
        int userId = 1;
        User executingUser = new User();
        List<Card> expectedCards = Collections.singletonList(new Card());
        Mockito.when(mockRepository.getAllCardsByUserId(userId)).thenReturn(Optional.of(expectedCards));

        // Act
        List<Card> actualCards = cardService.getAllCardsByUserId(executingUser);

        // Assert
        assertEquals(expectedCards, actualCards);
    }

    // Add more test cases for other methods as needed

    @Test
    public void get_Should_CallRepository() {
        Card mockCard = createMockCard();
        List<Card> expectedCards = Collections.singletonList(mockCard);
        Mockito.when(mockRepository.getAllCards()).thenReturn(expectedCards);

        cardService.getAllCards();

        Mockito.verify(mockRepository, Mockito.times(1)).getAllCards();

    }

//    @Test
//    public void getCardById_Should_ReturnCard_When_MatchExists() {
//        Card card = createMockCard();
//        User executingUser = createMockUser();
//
//        Mockito.when(mockRepository.getCardById(card.getId())).thenReturn(Optional.of(card));
//
//        cardService.getCardById(card.getId(), executingUser);
//
//        Mockito.verify(mockRepository, Mockito.times(1)).getCardById(card.getId());
//    }


    @Test
    void getCardById_Should_ThrowAuthorizationException_When_UserDoesNotHavePermission() {
        Card mockCard = createMockCard();
        User executingUser = createMockUser();

        assertThrows(
                AuthorizationException.class,
                () -> cardService.getCardById(mockCard.getId(), executingUser));
    }


    @Test
    public void getAllCardsByUserId_Should_ReturnAllCards_When_MatchExists() {
        User user = createMockUser();
        List<Card> expectedCards = Collections.singletonList(new Card());
        Mockito.when(mockRepository.getAllCardsByUserId(user.getId())).thenReturn(Optional.of(expectedCards));

        cardService.getAllCardsByUserId(user);

        Mockito.verify(mockRepository, Mockito.times(1)).getAllCardsByUserId(user.getId());
    }

    @Test
    public void getCardByCardNumber_Should_ReturnCard_When_MatchExists() {
        Card mockCard = createMockCard();
        Mockito.when(mockRepository.getByCardNumber(mockCard.getCardNumber())).thenReturn(Optional.of(mockCard));

        Card resultCard = cardService.getCardByCardNumber(mockCard.getCardNumber());

        Mockito.verify(mockRepository, Mockito.times(1)).getByCardNumber(mockCard.getCardNumber());

    }

    @Test
    public void addCard_Should_CallRepository_When_AddingNewCard() {
        Card card = createMockCard();
        User user = createMockUser();
        Wallet wallet = createMockWallet();
        Mockito.when(walletService.getWalletById(wallet.getId(), user.getId())).thenReturn(new Wallet());

        cardService.addCard(card, wallet.getId(), user);

        Mockito.verify(mockRepository, Mockito.times(1)).addCard(card);
    }
}

package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.CardMismatchException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.CardMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.CardDto;
import com.example.virtualwallet.services.contracts.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {

    private final CardService cardService;
    private final CardMapper cardMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CardRestController(CardService cardService, CardMapper cardMapper, AuthenticationHelper authenticationHelper) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards(@RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<Card> cards = cardService.getAllCards();
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCardById(@RequestHeader HttpHeaders headers, @PathVariable int cardId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card card = cardService.getCardById(cardId, user);
            return new ResponseEntity<>(card, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Card>> getAllCardsByUserId(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            List<Card> cards = cardService.getAllCardsByUserId(userId, user);
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/wallets/{walletId}")
    public ResponseEntity<Card> addCardToWallet(@RequestHeader HttpHeaders headers,
                                                @PathVariable int walletId,
                                                @Valid @RequestBody CardDto cardDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card cardToAdd = cardMapper.fromDto(cardDto, user);
            Card card = cardService.addCard(cardToAdd, walletId, user);
            return new ResponseEntity<>(card, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (CardMismatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(@RequestHeader HttpHeaders headers,
                                           @PathVariable int cardId,
                                           @Valid @RequestBody CardDto cardDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card cardToUpdate = cardMapper.fromDto(cardId, cardDto, user);
            cardService.updateCard(cardToUpdate, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (CardMismatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@RequestHeader HttpHeaders headers, @PathVariable int cardId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card cardToDelete = cardService.getCardById(cardId, user);
            cardService.deleteCard(cardToDelete.getId(), user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
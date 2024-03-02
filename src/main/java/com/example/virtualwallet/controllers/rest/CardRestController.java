package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {

    private final CardService cardService;
    private final WalletService walletService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CardRestController(CardService cardService, WalletService walletService, AuthenticationHelper authenticationHelper) {
        this.cardService = cardService;
        this.walletService = walletService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards(@RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            List<Card> cards = cardService.getAllCards();
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getCardById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card card = cardService.getCardById(id, user);
            return new ResponseEntity<>(card, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/default/{recipientUserId}")
    public ResponseEntity<Wallet> getDefaultWallet(@RequestHeader HttpHeaders headers, @PathVariable int recipientUserId) {
        try {
            authenticationHelper.tryGetUser(headers);
            Wallet wallet = walletService.getDefaultWallet(recipientUserId);
            return new ResponseEntity<>(wallet, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<Wallet> create(@RequestHeader HttpHeaders headers, @RequestBody Wallet wallet) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet createdWallet = walletService.create(wallet, user);
            return new ResponseEntity<>(createdWallet, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> update(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody Wallet wallet) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            wallet.setId(id);
            walletService.update(wallet, user);
            return new ResponseEntity<>(wallet, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(id, user.getId());
            walletService.delete(wallet, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/{walletId}/users/{userId}")
    public ResponseEntity<Void> addUserToWallet(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @PathVariable int userId, @RequestBody User user) {
        try {
            authenticationHelper.tryGetUser(headers);
            walletService.addUsersToWallet(walletId, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{walletId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromWallet(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @PathVariable int userId, @RequestBody User user) {
        try {
            authenticationHelper.tryGetUser(headers);
            walletService.removeUsersFromWallet(walletId, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
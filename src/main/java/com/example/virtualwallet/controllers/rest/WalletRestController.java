package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletRestController {

    private final WalletService walletService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public WalletRestController(WalletService walletService, AuthenticationHelper authenticationHelper) {
        this.walletService = walletService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets(@RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            List<Wallet> wallets = walletService.getAll(user);
            return new ResponseEntity<>(wallets, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(id, user.getId());
            return new ResponseEntity<>(wallet, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

//    @GetMapping("/default/{recipientUserId}")
//    public ResponseEntity<Wallet> getDefaultWallet(@RequestHeader HttpHeaders headers, @PathVariable int recipientUserId) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            Wallet wallet = walletService.getDefaultWallet(recipientUserId);
//            return new ResponseEntity<>(wallet, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }

//    @PostMapping
//    public ResponseEntity<Wallet> create(@RequestHeader HttpHeaders headers, @RequestBody WalletDto walletDto) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            Wallet createdWallet = walletService.create(walletDto);
//            return new ResponseEntity<>(createdWallet, HttpStatus.CREATED);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }

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
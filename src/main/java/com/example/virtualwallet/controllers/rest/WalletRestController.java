package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.WalletMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
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
    private final WalletMapper walletMapper;

    @Autowired
    public WalletRestController(WalletService walletService,
                                AuthenticationHelper authenticationHelper,
                                WalletMapper walletMapper) {
        this.walletService = walletService;
        this.authenticationHelper = authenticationHelper;
        this.walletMapper = walletMapper;
    }

    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets(@RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            List<Wallet> wallets = walletService.getAll(user);
            return new ResponseEntity<>(wallets, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(id, user.getId());
            return new ResponseEntity<>(wallet, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
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
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        } catch (AuthorizationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        }
//    }

    @PostMapping
    public ResponseEntity<Wallet> create(@RequestHeader HttpHeaders headers,
                                         @Valid @RequestBody WalletDto walletDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet createdWallet = walletMapper.fromDto(walletDto, user);
            walletService.create(createdWallet, user);
            return new ResponseEntity<>(createdWallet, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> update(@RequestHeader HttpHeaders headers,
                                         @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletToUpdate = walletService.getWalletById(id, user.getId());
            walletService.update(walletToUpdate, user);
            return new ResponseEntity<>(walletToUpdate, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletToDelete=walletService.getWalletById(id, user.getId());
            walletService.delete(walletToDelete, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/{walletId}/users/{userId}")
    public ResponseEntity<Void> addUserToWallet(@RequestHeader HttpHeaders headers,
                                                @PathVariable int walletId,
                                                @PathVariable int userId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            walletService.addUsersToWallet(walletId, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{walletId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromWallet(@RequestHeader HttpHeaders headers,
                                                     @PathVariable int walletId,
                                                     @PathVariable int userId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            walletService.removeUsersFromWallet(walletId, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
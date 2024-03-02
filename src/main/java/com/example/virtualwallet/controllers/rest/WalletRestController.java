package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.UserMapper;
import com.example.virtualwallet.helpers.WalletMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.UserDto;
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
    private final WalletMapper walletMapper;
    private final UserMapper userMapper;

    @Autowired
    public WalletRestController(WalletService walletService, AuthenticationHelper authenticationHelper, WalletMapper walletMapper, UserMapper userMapper) {
        this.walletService = walletService;
        this.authenticationHelper = authenticationHelper;
        this.walletMapper = walletMapper;
        this.userMapper = userMapper;
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
    public ResponseEntity<Wallet> create(@RequestHeader HttpHeaders headers, @RequestBody WalletDto walletDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet createdWallet = walletMapper.fromDtoRegister(walletDto);
            walletService.create(createdWallet, user);
            return new ResponseEntity<>(createdWallet, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> update(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody WalletDto walletDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet wallet = walletMapper.fromDtoRegister(walletDto);
            walletService.update(wallet, user);
            return new ResponseEntity<>(wallet, HttpStatus.OK);
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
            walletService.delete(id, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/{walletId}/users/{userId}")
    public ResponseEntity<Void> addUserToWallet(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @PathVariable int userId, @RequestBody UserDto userDto) {
        try {
            authenticationHelper.tryGetUser(headers);
            User user = userMapper.fromDtoRegister(userDto);
            walletService.addUsersToWallet(walletId, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{walletId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromWallet(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @PathVariable int userId, @RequestBody User user) {
        try {
            authenticationHelper.tryGetUser(headers);
            walletService.removeUsersFromWallet(walletId, userId, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.TransactionMapper;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionMapper transactionMapper;
    private final WalletService walletService;

    @Autowired
    public TransactionRestController(TransactionService transferService,
                                     AuthenticationHelper authenticationHelper, TransactionMapper transactionMapper, WalletService walletService) {
        this.transactionService = transferService;
        this.authenticationHelper = authenticationHelper;
        this.transactionMapper = transactionMapper;
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<Transaction> transactions = transactionService.getAllTransactions();
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Transaction transaction = transactionService.getTransactionById(id);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

//    @PostMapping("/{walletId}")
//    public ResponseEntity<Transaction> confirmTransaction(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @RequestBody TransactionDto transactionDto) {
//        try {
//            User user = authenticationHelper.tryGetUser(headers);
//            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
//            Transaction transaction = transactionMapper.fromDtoMoneyOut(walletSender, transactionDto, user);
//            transactionService.updateTransaction(transaction, user);
//            return new ResponseEntity<>(transaction, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        } catch (AuthorizationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        }
//    }

    @PostMapping("/{walletId}/send")
    public ResponseEntity<Transaction> createTransaction(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(walletSender, transactionDto, user);
            transactionService.createTransaction(transaction, walletSender, user);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/wallets/{walletId}")
    public ResponseEntity<Transaction> updateTransaction(@RequestHeader HttpHeaders headers, @PathVariable int id, @PathVariable int walletId, @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            Transaction transaction = transactionMapper.fromDto(id, walletSender, transactionDto, user);
            transactionService.updateTransaction(transaction, walletSender, user);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    //TODO implement mvc view with option for update and go back
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Transaction transaction = transactionService.getTransactionById(id);
            transactionService.delete(transaction, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    //TODO getTransactionsStatusById
    @PostMapping("/{walletId}/request")
    public ResponseEntity<Transaction> requestTransaction(@RequestHeader HttpHeaders headers, @PathVariable int walletId, @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet receiverWallet = walletService.getWalletById(walletId, user.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(receiverWallet, transactionDto, user);
            transactionService.requestMoney(transaction, receiverWallet, user);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, e.getMessage());
        }//Todo add check to MVC for INVALID_REQUEST
    }
}
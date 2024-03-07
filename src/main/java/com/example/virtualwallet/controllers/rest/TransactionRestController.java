package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.TransactionMapper;
import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.RecurringTransactionDto;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
    private final RecurringTransactionService recurringTransactionService;
    private final WalletService walletService;
    private final UserService userService;

    @Autowired
    public TransactionRestController(TransactionService transferService,
                                     AuthenticationHelper authenticationHelper,
                                     TransactionMapper transactionMapper,
                                     RecurringTransactionService recurringTransactionService,
                                     WalletService walletService, UserService userService) {
        this.transactionService = transferService;
        this.authenticationHelper = authenticationHelper;
        this.transactionMapper = transactionMapper;
        this.recurringTransactionService = recurringTransactionService;
        this.walletService = walletService;
        this.userService = userService;
    }

    @GetMapping
    public List<Transaction> getTransactions(@RequestHeader HttpHeaders headers,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {
        try {
            authenticationHelper.tryGetUser(headers);
            return transactionService.getAll(PageRequest.of(page, size)).getContent();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

//    @GetMapping
//    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestHeader HttpHeaders headers) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            List<Transaction> transactions = transactionService.getAllTransactions();
//            return new ResponseEntity<>(transactions, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        } catch (AuthorizationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        }
//    }

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

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByWalletId(@RequestHeader HttpHeaders headers,
                                                                          @PathVariable int walletId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            List<Transaction> transactions = transactionService.getAllTransactionsByWalletId(wallet);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/{walletId}")
    public ResponseEntity<Transaction> confirmTransaction(@RequestHeader HttpHeaders headers,
                                                          @PathVariable int walletId,
                                                          @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getByUsername(transactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, user,
                    walletReceiver, userReceiver);
            transactionService.confirmTransaction(transaction, walletSender, user);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/wallets/{walletId}/send")
    public ResponseEntity<Transaction> createTransaction(@RequestHeader HttpHeaders headers,
                                                         @PathVariable int walletId,
                                                         @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getByUsername(transactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, user,
                    walletReceiver, userReceiver);
            transactionService.createTransaction(transaction, walletSender, user, walletReceiver, userReceiver);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<Transaction> updateTransaction(@RequestHeader HttpHeaders headers,
                                                         @PathVariable int transactionId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Transaction transaction = transactionService.getTransactionById(transactionId);
//            Transaction transaction = transactionMapper.fromDto(id,transactionDto,walletSender,user,
//                    walletReceiver,userReceiver);
            transactionService.updateTransaction(transaction, user);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
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
    @PostMapping("/wallets/{walletId}/request")
    public ResponseEntity<Transaction> requestTransaction(@RequestHeader HttpHeaders headers,
                                                          @PathVariable int walletId,
                                                          @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletReceiver = walletService.getWalletById(walletId, user.getId());
            User userSender = userService.getByUsername(transactionDto.getReceiver());
            Wallet walletSender = walletService.getDefaultWallet(userSender.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, userSender,
                    walletReceiver, user);
            transactionService.requestMoney(transaction, walletReceiver, user);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }//Todo add check to MVC for INVALID_REQUEST
    }

    @PostMapping("/wallets/{walletId}/recurring")
    public ResponseEntity<RecurringTransaction> createRecurringTransaction(@RequestHeader HttpHeaders headers,
                                                                           @PathVariable int walletId,
                                                                           @RequestBody RecurringTransactionDto
                                                                                   recurringTransactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getByUsername(recurringTransactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            RecurringTransaction recurringTransaction = transactionMapper.fromDtoTransaction(recurringTransactionDto, walletSender,
                    user, walletReceiver, userReceiver);
            recurringTransactionService.createRecurringTransaction(recurringTransaction,
                    walletSender, user, walletReceiver, userReceiver);
            return new ResponseEntity<>(recurringTransaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
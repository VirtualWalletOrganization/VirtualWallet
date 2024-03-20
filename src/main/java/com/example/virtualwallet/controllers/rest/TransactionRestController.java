package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.TransactionMapper;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.RecurringTransactionDto;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(tags ={"Get all transactions"},
            summary = "This method retrieve information about all transactions.",
            description = "This method search for all transactions in the data base. When a person is authenticated and there are transactions, a list with them will be presented.",
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "Transactions were found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "You are not allowed to access the list of transactions."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "Transactions were not found.")})
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestHeader HttpHeaders headers,
                                                                @RequestParam TransactionFilterOptions transactionFilterOptions) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<Transaction> transactions = transactionService.getAllTransactions(transactionFilterOptions);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(tags = {"Get a transaction"},
            operationId = "id to be searched for",
            summary = "This method search for a particular transaction when id is given.",
            description = "This method search for a transaction. A valid id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "id", description = "contact id", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "The transaction has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "You are not allowed to access this transaction."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "Transaction with this id was not found.")})
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
    @Operation(tags = {"Get all transactions by wallet id"},
            operationId = "id to be searched for",
            summary = "This method search for all transactions when wallet id is given.",
            description = "This method search for all transactions related to a particular id. A valid wallet id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "id", description = "wallet id", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "The transactions have been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "You are not allowed to access these transactions."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "Wallet with this id was not found.")})
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
    @Operation(tags = {"Confirm transaction"},
            summary = "With this method a transaction is being confirmed.",
            description = "With this method a transaction is being confirmed. A valid object must be given as an input and wallet id. Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = Transaction.class))),
            operationId = "wallet id",
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "The transaction has been confirmed successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "You are not allowed to confirm this transaction."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "Wallet with this id was not found.")})
    public ResponseEntity<Transaction> confirmTransaction(@RequestHeader HttpHeaders headers,
                                                          @PathVariable int walletId,
                                                          @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getById(transactionDto.getReceiver());
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
    @Operation(tags = {"Create a transaction"},
            summary = "This method creates a transaction when input is given.",
            description = "This method creates a transaction. A valid object must be given as an input. Proper authentication must be in place",
            operationId = "wallet id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = Transaction.class))),
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "The transaction has been created successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "You are not allowed to create a transaction."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Transaction.class)), description = "Wallet with this id was not found.")})
    public ResponseEntity<Transaction> createTransaction(@RequestHeader HttpHeaders headers,
                                                         @PathVariable int walletId,
                                                         @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getById(transactionDto.getReceiver());
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
    public ResponseEntity<Transaction> completeRequestTransaction(@RequestHeader HttpHeaders headers,
                                                                  @PathVariable int transactionId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Transaction transaction = transactionService.getTransactionById(transactionId);
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

    @PostMapping("/wallets/{walletId}/request")
    public ResponseEntity<Transaction> requestTransaction(@RequestHeader HttpHeaders headers,
                                                          @PathVariable int walletId,
                                                          @RequestBody TransactionDto transactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet walletReceiver = walletService.getWalletById(walletId, user.getId());
            User userSender = userService.getById(transactionDto.getReceiver());
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
            User userReceiver = userService.getById(recurringTransactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            RecurringTransaction recurringTransaction = transactionMapper.fromDtoTransaction(recurringTransactionDto,
                    walletSender,
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

    @PutMapping("/recurring/{recurringId}")
    public ResponseEntity<RecurringTransaction> updateRecurringTransaction(@RequestHeader HttpHeaders headers,
                                                                           @PathVariable int recurringId,
                                                                           @RequestBody RecurringTransactionDto
                                                                                   recurringTransactionDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            RecurringTransaction existingRecurring = recurringTransactionService.getRecurringTransactionById(recurringId);
            RecurringTransaction recurringTransaction = transactionMapper
                    .fromDtoTransactionUpdate(recurringTransactionDto, existingRecurring);
            recurringTransactionService.updateRecurringTransaction(recurringTransaction, user);
            return new ResponseEntity<>(recurringTransaction, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/recurring/{recurringId}")
    public ResponseEntity<RecurringTransaction> cancelRecurringTransaction(@RequestHeader HttpHeaders headers,
                                                                           @PathVariable int recurringId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            RecurringTransaction recurringTransaction = recurringTransactionService
                    .getRecurringTransactionById(recurringId);
            recurringTransactionService.cancelRecurringTransaction(recurringTransaction, user);
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
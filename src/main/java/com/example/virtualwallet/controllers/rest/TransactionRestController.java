//package com.example.virtualwallet.controllers.rest;
//
//import com.example.virtualwallet.exceptions.AuthorizationException;
//import com.example.virtualwallet.exceptions.EntityNotFoundException;
//import com.example.virtualwallet.exceptions.InsufficientBalanceException;
//import com.example.virtualwallet.helpers.AuthenticationHelper;
//import com.example.virtualwallet.models.Transaction;
//import com.example.virtualwallet.models.Transfer;
//import com.example.virtualwallet.models.dtos.TransactionDto;
//import com.example.virtualwallet.services.contracts.TransactionService;
//import com.example.virtualwallet.services.contracts.TransferService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//@RestController
//@RequestMapping("/api/transactions")
//public class TransactionRestController {
//    private final TransactionService transactionService;
//    private final AuthenticationHelper authenticationHelper;
//
//    @Autowired
//    public TransactionRestController(TransactionService transferService,
//                                  AuthenticationHelper authenticationHelper) {
//        this.transactionService = transferService;
//        this.authenticationHelper = authenticationHelper;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Transaction>> getAllTransaction(@RequestHeader HttpHeaders headers) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            List<Transaction> transactions = transactionService.getAllTransfers();
//            return new ResponseEntity<>(transactions, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Transaction> getTransferById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            Transaction transaction = transactionService.getTransactionById(id);
//            return new ResponseEntity<>(transaction, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @PostMapping
//    public ResponseEntity<Transaction> createTransfer(@RequestHeader HttpHeaders headers, @RequestBody TransactionDto transactionDto) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            Transaction transaction= new Transaction();
//
//            Transaction createdTransaction = transactionService.createTransaction(transactionDto);
//            return new ResponseEntity<>(createdTransfer, HttpStatus.CREATED);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Transfer> updateTransfer(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody Transfer transfer) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            transfer.setId(id);
//            transferService.updateTransfer(transfer);
//            return new ResponseEntity<>(transfer, HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteTransfer(@RequestHeader HttpHeaders headers, @PathVariable int id) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            Transfer transfer = transferService.getTransferById(id);
//            transferService.deleteTransfer(transfer);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @PostMapping("/transfer-money")
//    public ResponseEntity<Void> transferMoney(@RequestHeader HttpHeaders headers, @RequestParam int senderUserId, @RequestParam int recipientUserId, @RequestParam int walletId, @RequestParam double amount) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            transferService.transferMoney(senderUserId, recipientUserId, walletId, amount);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (EntityNotFoundException | InsufficientBalanceException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @PostMapping("/confirm-transfer")
//    public ResponseEntity<Void> confirmTransfer(@RequestHeader HttpHeaders headers, @RequestParam int transferId, @RequestParam int senderId, @RequestParam int recipientId, @RequestParam int recipientWalletId) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            transferService.confirmTransfer(transferId, senderId, recipientId, recipientWalletId);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    @PostMapping("/edit-transfer")
//    public ResponseEntity<Void> editTransfer(@RequestHeader HttpHeaders headers, @RequestParam int transferId, @RequestParam double newAmount) {
//        try {
//            authenticationHelper.tryGetUser(headers);
//            transferService.editTransfer(transferId, newAmount);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (AuthorizationException e) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//}

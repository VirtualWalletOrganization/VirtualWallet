package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.services.contracts.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferRestController {

    private final TransferService transferService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public TransferRestController(TransferService transferService, AuthenticationHelper authenticationHelper) {
        this.transferService = transferService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> getAllTransfers(@RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<Transfer> transfers = transferService.getAllTransfers();
            return new ResponseEntity<>(transfers, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transfer> getTransferById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Transfer transfer = transferService.getTransferById(id);
            return new ResponseEntity<>(transfer, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<Transfer> createTransfer(@RequestHeader HttpHeaders headers, @RequestBody Transfer transfer) {
        try {
            authenticationHelper.tryGetUser(headers);
            Transfer createdTransfer = transferService.createTransfer(transfer);
            return new ResponseEntity<>(createdTransfer, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transfer> updateTransfer(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody Transfer transfer) {
        try {
            authenticationHelper.tryGetUser(headers);
            transfer.setId(id);
            transferService.updateTransfer(transfer);
            return new ResponseEntity<>(transfer, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Transfer transfer = transferService.getTransferById(id);
            transferService.deleteTransfer(transfer);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/transfer-money")
    public ResponseEntity<Void> transferMoney(@RequestHeader HttpHeaders headers, @RequestParam int senderUserId, @RequestParam int recipientUserId, @RequestParam int walletId, @RequestParam double amount) {
        try {
            authenticationHelper.tryGetUser(headers);
            transferService.transferMoney(senderUserId, recipientUserId, walletId, amount);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException | InsufficientBalanceException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/confirm-transfer")
    public ResponseEntity<Void> confirmTransfer(@RequestHeader HttpHeaders headers, @RequestParam int transferId, @RequestParam int senderId, @RequestParam int recipientId, @RequestParam int recipientWalletId) {
        try {
            authenticationHelper.tryGetUser(headers);
            transferService.confirmTransfer(transferId, senderId, recipientId, recipientWalletId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/edit-transfer")
    public ResponseEntity<Void> editTransfer(@RequestHeader HttpHeaders headers, @RequestParam int transferId, @RequestParam double newAmount) {
        try {
            authenticationHelper.tryGetUser(headers);
            transferService.editTransfer(transferId, newAmount);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
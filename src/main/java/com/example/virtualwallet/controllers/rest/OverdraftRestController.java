package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Overdraft;
import com.example.virtualwallet.services.contracts.OverdraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/overdrafts")
public class OverdraftRestController {

    private final OverdraftService overdraftService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public OverdraftRestController(OverdraftService overdraftService, AuthenticationHelper authenticationHelper) {
        this.overdraftService = overdraftService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public ResponseEntity<List<Overdraft>> getAllOverdrafts(@RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<Overdraft> overdrafts = overdraftService.getAllOverdrafts();
            return new ResponseEntity<>(overdrafts, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Overdraft> getOverdraftById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Overdraft overdraft = overdraftService.getOverdraftById(id);
            return new ResponseEntity<>(overdraft, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Overdraft> create(@RequestHeader HttpHeaders headers, @RequestBody Overdraft overdraft) {
        try {
            authenticationHelper.tryGetUser(headers);
            Overdraft createdOverdraft = overdraftService.create(overdraft);
            return new ResponseEntity<>(createdOverdraft, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Overdraft> update(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody Overdraft overdraft) {
        try {
            authenticationHelper.tryGetUser(headers);
            overdraft.setId(id);
            overdraftService.update(overdraft);
            return new ResponseEntity<>(overdraft, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Overdraft overdraft = overdraftService.getOverdraftById(id);
            overdraftService.delete(overdraft);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/paid")
    public ResponseEntity<Void> updatePaidStatus(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestParam boolean isPaid) {
        try {
            authenticationHelper.tryGetUser(headers);
            overdraftService.updatePaidStatus(id, isPaid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
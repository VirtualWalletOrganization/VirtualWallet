package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.ContactService;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactRestController {

    private final ContactService contactService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;

    @Autowired
    public ContactRestController(ContactService contactService, AuthenticationHelper authenticationHelper, UserService userService) {
        this.contactService = contactService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Contact contact = contactService.getContactById(id);
            return new ResponseEntity<>(contact, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Contact>> getAllContactsByUserId(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<Contact> contacts = contactService.getAllContactsByUserId(userId);
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Contact> createContact(@RequestHeader HttpHeaders headers, @PathVariable int userId, @RequestBody Contact contact) {
        try {
            authenticationHelper.tryGetUser(headers);
            User user = userService.getById(userId);
            contactService.create(user, contact);
            return new ResponseEntity<>(contact, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody Contact contact) {
        try {
            authenticationHelper.tryGetUser(headers);
            contact.setContactId(id);
            contactService.update(contact);
            return new ResponseEntity<>(contact, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            contactService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.ContactService;
import com.example.virtualwallet.services.contracts.UserService;
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
@RequestMapping("/api/contacts")
public class ContactRestController {

    private final ContactService contactService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;

    @Autowired
    public ContactRestController(ContactService contactService,
                                 AuthenticationHelper authenticationHelper, UserService userService) {
        this.contactService = contactService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(tags = {"Get a contact"},
            operationId = "id to be searched for",
            summary = "This method search for a contact when id is given.",
            description = "This method search for a contact. A valid id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "id", description = "contact id", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Contact.class)), description = "The contact has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Contact.class)), description = "You are not allowed to access this contact."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Contact.class)), description = "Contact with this id was not found.")})
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
    @Operation(tags = {"Get all contacts by user id"},
            operationId = "id to be searched for",
            summary = "This method search for all contacts when id is given.",
            description = "This method search for all contacts. A valid id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "id", description = "user id", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Contact.class)), description = "The contacts have been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Contact.class)), description = "You are not allowed to access these contacts."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Contact.class)), description = "User with this id was not found.")})
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
    @Operation(tags = {"Create a contact"},
            summary = "This method creates a contact when input is given.",
            description = "This method create a post. A valid object must be given as an input. Proper authentication must be in place",
            operationId = "user id",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = Contact.class))),
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Contact.class)), description = "The contact has been created successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Contact.class)), description = "You are not allowed to create a contact."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Contact.class)), description = "User with this id was not found.")})
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
    @Operation(tags = {"Update a contact"},
            operationId = "id to be updated",
            summary = "This method update a contact when id is given.",
            description = "This method update a contact. A valid object must be given as an input.Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts Contact parameters.",
                    content = @Content(schema = @Schema(implementation = Contact.class))),
            parameters = {@Parameter(name = "contactId", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Contact.class)), description = "The contact has been updated successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Contact.class)), description = "You are not allowed to modify this contact."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Contact.class)), description = "Contact with this id was not found.")})
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
    @Operation(tags = {"Delete a contact"},
            operationId = "id to be deleted",
            summary = "This method deletes a contact when id is given.",
            description = "This method deletes a contact. A valid id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "postId", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Contact.class)), description = "The post has been deleted successfully"),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Contact.class)), description = "The post with this id was not found."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Contact.class)), description = "You are not allowed to delete this post.")})
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
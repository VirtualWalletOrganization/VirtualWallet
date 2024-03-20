package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Overdraft;
import com.example.virtualwallet.services.contracts.OverdraftService;
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
    @Operation(tags = {"Get all overdrafts"},
            summary = "This method retrieve information about all overdrafts.",
            description = "This method search for all overdrafts in the data base. When a person is authorized and there are overdrafts, a list with all overdrafts will be presented.",
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "Overdraft(s) was/were found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "You are not allowed to access the list of overdrafts."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "Overdraft(s) is/were not found.")})
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
    @Operation(tags = {"Get an overdraft"},
            operationId = "Id to be searched for",
            summary = "This method search for a overdraft when id is given.",
            description = "This method search for an overdraft. A valid id must be given as an input.",
            parameters = {@Parameter(name = "id", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "The overdraft has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "You are not allowed to access this overdraft."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "Overdraft with this id was not found.")})
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
    @Operation(tags = {"Create an overdraft"},
            summary = "This method creates an overdraft.",
            description = "This method creates an overdraft. A valid object must be given as an input. Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = Overdraft.class))),
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "The overdraft has been created successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "You are not allowed to create an overdraft."),})
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
    @Operation(tags = {"Update an overdraft"},
            operationId = "id to be updated",
            summary = "This method updates an overdraft when id is given.",
            description = "This method updates an overdraft. A valid object must be given as an input.Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts Overdraft parameters.",
                    content = @Content(schema = @Schema(implementation = Overdraft.class))),
            parameters = {@Parameter(name = "contactId", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "The overdraft has been updated successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "You are not allowed to modify this overdraft."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "Overdraft with this id was not found.")})
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
    @Operation(tags = {"Delete an overdraft"},
            operationId = "id to be deleted",
            summary = "This method deletes an overdraft when id is given.",
            description = "This method deletes an overdraft. A valid id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "postId", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "The post has been deleted successfully"),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "The post with this id was not found."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "You are not allowed to delete this post.")})
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
    @Operation(tags = {"Update payment status"},
            operationId = "id to be updated",
            summary = "This method updates payment status when id is given.",
            description = "This method updates the payment status. A valid id must be given as an input.Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts boolean isPaid",
                    content = @Content(schema = @Schema(implementation = Overdraft.class))),
            parameters = {@Parameter(name = "overdrafttId", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "The payment status has been updated successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "You are not allowed to modify the maypent status for this overdraft."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Overdraft.class)), description = "Overdraft with this id was not found.")})
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
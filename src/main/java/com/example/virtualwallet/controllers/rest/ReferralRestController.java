package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.services.contracts.ReferralService;
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

import java.sql.Ref;

@RestController
@RequestMapping("/api/referrals")
public class ReferralRestController {

    private final ReferralService referralService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;

    @Autowired
    public ReferralRestController(ReferralService referralService, AuthenticationHelper authenticationHelper, UserService userService) {
        this.referralService = referralService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(tags = {"Get a teferral"},
            operationId = "id to be searched for",
            summary = "This method search for a referral when id is given.",
            description = "This method search for a referral. A valid id must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "id", description = "referral id", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Referral.class)), description = "You are not allowed to access this referral."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Referral.class)), description = "Referral with this id was not found.")})
    public ResponseEntity<Referral> getReferralById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Referral referral = referralService.getById(id);
            return new ResponseEntity<>(referral, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    @Operation(tags = {"Create a referral"},
            summary = "This method creates a referral when input is given.",
            description = "This method creates a referral. A valid object must be given as an input. Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = Contact.class))),
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral has been created successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Referral.class)), description = "You are not allowed to create a referral."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Referral.class)), description = "Referral with this id was not found.")})
    public ResponseEntity<Referral> create(@RequestHeader HttpHeaders headers, @RequestBody Referral referral) {
        try {
            authenticationHelper.tryGetUser(headers);
            Referral createdReferral = referralService.create(referral);
            return new ResponseEntity<>(createdReferral, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/refer")
    @Operation(tags = {"Refer a friend"},
            summary = "With this method, a user can refer a friend..",
            description = "Refer a friend method. A valid object must be given as an input. Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = Contact.class))),
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral has been created successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Referral.class)), description = "You are not allowed to create a referral."),})
    public ResponseEntity<Void> referFriend(@RequestHeader HttpHeaders headers, @RequestBody Referral referral) {
        try {
            authenticationHelper.tryGetUser(headers);
            User user = userService.getByEmail(referral.getUser().getEmail());
            referralService.referFriend(user, referral.getReferredEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/status")
    @Operation(tags = {"Get referral status by email"},
            operationId = "email to be searched for",
            summary = "This method search for a referral status when email is given.",
            description = "This method search for a referral status. A valid email must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "email", description = "valid email address", example = "example@abv.bg")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Referral.class)), description = "You are not allowed to access this referral."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral was not found.")})
    public ResponseEntity<Status> getReferralStatusByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            authenticationHelper.tryGetUser(headers);
            Status status = referralService.getReferralStatusByEmail(email);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/referrer")
    @Operation(tags = {"Get referrae user id by email"},
            operationId = "email to be searched for",
            summary = "This method search for a referrer user id when email is given.",
            description = "This method search for a referrer user id. A valid email must be given as an input. Proper authentication must be in place",
            parameters = {@Parameter(name = "email", description = "valid email address", example = "example@abv.bg")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Referral.class)), description = "You are not allowed to access this referral."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Referral.class)), description = "The referral was not found.")})
    public ResponseEntity<User> getReferrerUserIdByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            authenticationHelper.tryGetUser(headers);
            User user = referralService.getReferrerUserIdByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
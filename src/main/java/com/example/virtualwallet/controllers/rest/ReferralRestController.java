package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Referral;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.services.contracts.ReferralService;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Referral> getReferralById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            Referral referral = referralService.getById(id);
            return new ResponseEntity<>(referral, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<Referral> create(@RequestHeader HttpHeaders headers, @RequestBody Referral referral) {
        try {
            authenticationHelper.tryGetUser(headers);
            Referral createdReferral = referralService.create(referral);
            return new ResponseEntity<>(createdReferral, HttpStatus.CREATED);
        } catch (DuplicateEntityException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refer")
    public ResponseEntity<Void> referFriend(@RequestHeader HttpHeaders headers, @RequestBody Referral referral) {
        try {
            authenticationHelper.tryGetUser(headers);
            User user = userService.getByEmail(referral.getUser().getEmail());
            referralService.referFriend(user, referral.getReferredEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (DuplicateEntityException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Status> getReferralStatusByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            authenticationHelper.tryGetUser(headers);
            Status status = referralService.getReferralStatusByEmail(email);
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/referrer")
    public ResponseEntity<User> getReferrerUserIdByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            authenticationHelper.tryGetUser(headers);
            User user = referralService.getReferrerUserIdByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
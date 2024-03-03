package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.services.contracts.SpendingCategoryService;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/spending-categories")
public class SpendingCategoryRestController {

    private final SpendingCategoryService spendingCategoryService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;

    @Autowired
    public SpendingCategoryRestController(SpendingCategoryService spendingCategoryService, AuthenticationHelper authenticationHelper, UserService userService) {
        this.spendingCategoryService = spendingCategoryService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<SpendingCategory>> getAllSpendingCategories(@RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            List<SpendingCategory> spendingCategories = spendingCategoryService.getAllSpendingCategories();
            return new ResponseEntity<>(spendingCategories, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<SpendingCategory> createSpendingCategory(@RequestHeader HttpHeaders headers, @RequestParam int userId, @RequestBody SpendingCategory category) {
        try {
            authenticationHelper.tryGetUser(headers);
//            category.setUser(user);
            SpendingCategory createdCategory = spendingCategoryService.createSpendingCategory(category);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<SpendingCategory> updateSpendingCategory(@RequestHeader HttpHeaders headers, @RequestParam int userId, @PathVariable int categoryId, @RequestBody SpendingCategory category) {
        try {
            authenticationHelper.tryGetUser(headers);
//            SpendingCategory existingCategory = spendingCategoryService.getSpendingCategoryById(categoryId);
//
//            if (existingCategory.getUser().getId() != userId) {
//                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//            }

            category.setId(categoryId);
//            category.setUser(user);
            SpendingCategory updatedCategory = spendingCategoryService.updateSpendingCategory(category);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteSpendingCategory(@RequestHeader HttpHeaders headers, @RequestParam int userId, @PathVariable int categoryId) {
        try {
            authenticationHelper.tryGetUser(headers);
//            SpendingCategory existingCategory = spendingCategoryService.getSpendingCategoryById(categoryId);
//
//            if (existingCategory.getUser().getId() != userId) {
//                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//            }

            spendingCategoryService.deleteSpendingCategory(categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
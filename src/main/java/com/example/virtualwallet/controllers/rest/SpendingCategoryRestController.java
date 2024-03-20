package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Contact;
import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.services.contracts.SpendingCategoryService;
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
@RequestMapping("/api/spending-categories")
public class SpendingCategoryRestController {

    private final SpendingCategoryService spendingCategoryService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public SpendingCategoryRestController(SpendingCategoryService spendingCategoryService,
                                          AuthenticationHelper authenticationHelper) {
        this.spendingCategoryService = spendingCategoryService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    @Operation(tags = {"Get all spending categories"},
            summary = "This method retrieve information about all spending categories.",
            description = "This method search for all spending categories in the data base. When a person is authenticated and there are spending categories, a list with all spending categories will be presented.",
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "Spending categories were found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "You are not allowed to access the list of spending categories."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "Spending categories were not found.")})
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
    @Operation(tags = {"Create a spending category"},
            summary = "This method creates a spending category when input is given.",
            description = "This method creates a spending category. A valid object must be given as an input and user id. Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts object parameters.",
                    content = @Content(schema = @Schema(implementation = SpendingCategory.class))),
            operationId = "user id",
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "The contact has been created successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "You are not allowed to create a contact."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "User with this id was not found.")})
    public ResponseEntity<SpendingCategory> createSpendingCategory(@RequestHeader HttpHeaders headers, @RequestParam int userId, @RequestBody SpendingCategory category) {
        try {
            authenticationHelper.tryGetUser(headers);
            SpendingCategory createdCategory = spendingCategoryService.createSpendingCategory(category);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{categoryId}")
    @Operation(tags = {"Update spending category"},
            operationId = "id to be updated",
            summary = "This method updates a spending category when id is given.",
            description = "This method updates a spending category. A valid object must be given as an input.Proper authentication must be in place",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts Spending category parameters.",
                    content = @Content(schema = @Schema(implementation = Contact.class))),
            parameters = {@Parameter(name = "userId", description = "path variable", example = "1")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "The spending category has been updated successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "You are not allowed to modify this spending category."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "Spending category with this id was not found.")})
    public ResponseEntity<SpendingCategory> updateSpendingCategory(@RequestHeader HttpHeaders headers, @RequestParam int userId, @PathVariable int categoryId, @RequestBody SpendingCategory category) {
        try {
            authenticationHelper.tryGetUser(headers);

            category.setId(categoryId);
            SpendingCategory updatedCategory = spendingCategoryService.updateSpendingCategory(category);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    @Operation(tags = {"Delete spending category"},
            operationId = "id to be deleted",
            summary = "This method deletes a spending category when id is given.",
            description = "This method deletes a spending category. A valid id must be given as an input. Proper authentication must be in place",

            parameters = {@Parameter(name = "categoryId", description = "path variable", example = "1")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "The spending category has been deleted successfully"),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "The spending category with this id was not found."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = SpendingCategory.class)), description = "You are not allowed to delete this spendning category.")})
    public ResponseEntity<Void> deleteSpendingCategory(@RequestHeader HttpHeaders headers, @RequestParam int userId, @PathVariable int categoryId) {
        try {
            authenticationHelper.tryGetUser(headers);

            spendingCategoryService.deleteSpendingCategory(categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
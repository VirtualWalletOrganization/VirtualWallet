package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.CardMismatchException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.CardMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.CardDto;
import com.example.virtualwallet.services.contracts.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardRestController {

    private final CardService cardService;
    private final CardMapper cardMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CardRestController(CardService cardService,
                              CardMapper cardMapper,
                              AuthenticationHelper authenticationHelper) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/{cardId}")
    @Operation(tags = {"Get a card"},
            operationId = "Id to be searched for",
            summary = "This method search for a card when id is given.",
            description = "This method search for a card. A valid id must be given as an input and proper authentication must be in place.",
            parameters = {@Parameter(name = "id", description = "path variable", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Card.class)), description = "The card has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Card.class)), description = "You are not allowed to access this card."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Card.class)), description = "Card with this id was not found.")})
    public ResponseEntity<Card> getCardById(@RequestHeader HttpHeaders headers, @PathVariable int cardId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card card = cardService.getCardById(cardId, user);
            return new ResponseEntity<>(card, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping
    @Operation(tags = {"Get cards"},
            summary = "This method search for cards.",
            description = "This method search for cards. A valid authentication must be in place.",
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Card.class)), description = "The cards have been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Card.class)), description = "You are not allowed to access these cards."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Card.class)), description = "No cards for this user were found")})
    public ResponseEntity<List<Card>> getAllCardsByCurrentUser(@RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            List<Card> cards = cardService.getAllCardsByCurrentUser(user);
            return new ResponseEntity<>(cards, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/wallets/{walletId}")
    @Operation(tags = {"Add card to a wallet"},
            summary = "This method adds card to a wallet when wallet id is given.",
            description = "This method adds card to a wallet. A valid object must be given as an input and proper authentication must be in place.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request that accepts tag dto object as a parameters.",
                    content = @Content(schema = @Schema(implementation = Card.class))),
            parameters = {@Parameter(name = "walletId", description = "Wallet id", example = "1")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Card.class)), description = "The card was added to the wallet successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Card.class)), description = "You are not allowed to add a card to this wallet."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Card.class)), description = "The wallet with this id was not found")})
    public ResponseEntity<Card> addCardToWallet(@RequestHeader HttpHeaders headers,
                                                @PathVariable int walletId,
                                                @Valid @RequestBody CardDto cardDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card cardToAdd = cardMapper.fromDto(cardDto, user);
            cardService.addCard(cardToAdd, walletId, user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (CardMismatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{cardId}")
    @Operation(tags = {"Update a card"},
            operationId = "id to be updated",
            summary = "This method update a card when id is given.",
            description = "This method update a card. Valid post id must be given as an input and proper authentication must be in place.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts cardDto parameters.",
                    content = @Content(schema = @Schema(implementation = Card.class))),
            parameters = {@Parameter(name = "id", description = "card id", example = "5")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Card.class)), description = "The card has been updated successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Card.class)), description = "You are not allowed to modify this card."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Card.class)), description = "Card with this id was not found.")})
    public ResponseEntity<Card> updateCard(@RequestHeader HttpHeaders headers,
                                           @PathVariable int cardId,
                                           @Valid @RequestBody CardDto cardDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card cardToUpdate = cardMapper.fromDto(cardId, cardDto, user);
            cardService.updateCard(cardToUpdate, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (CardMismatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{cardId}")
    @Operation(tags = {"Delete a card"},
            summary = "Using this method, a card is being deleted.",
            description = "This method deletes a card. When valid id is given as an input and proper authentication is in place, the card is deleted.",
            parameters = {@Parameter(name = "id", description = "card's id", example = "1")},
            responses = {@ApiResponse(responseCode = "410", content = @Content(schema = @Schema(implementation = Card.class)), description = "The card has been deleted successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = Card.class)), description = "You are not allowed to delete this card."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = Card.class)), description = "Card with this id was not found.")})
    public ResponseEntity<Void> delete(@RequestHeader HttpHeaders headers, @PathVariable int cardId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Card cardToDelete = cardService.getCardById(cardId, user);
            cardService.deleteCard(cardToDelete.getId(), user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
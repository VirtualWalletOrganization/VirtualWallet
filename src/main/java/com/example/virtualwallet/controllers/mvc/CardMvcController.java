package com.example.virtualwallet.controllers.mvc;

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
import com.example.virtualwallet.services.contracts.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cards")
public class CardMvcController {

    private final CardService cardService;
    private final CardMapper cardMapper;
    private final AuthenticationHelper authenticationHelper;
    private final WalletService walletService;

    @Autowired
    public CardMvcController(CardService cardService,
                             CardMapper cardMapper,
                             AuthenticationHelper authenticationHelper,
                             WalletService walletService) {
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.authenticationHelper = authenticationHelper;
        this.walletService = walletService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

//    @GetMapping
//    public String showAllCards(Model model, HttpSession session) {
//        try {
//            User user = authenticationHelper.tryGetCurrentUser(session);
//
//            if (user != null) {
//                model.addAttribute("currentUser", user);
//            }
//
//            List<Card> cards = cardService.getAllCards();
//            model.addAttribute("cards", cards);
//            return "cards";
//        } catch (AuthorizationException e) {
//            return "cards";
//        } catch (EntityNotFoundException e) {
//            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
//            model.addAttribute("error", e.getMessage());
//            return "error";
//        }
//    }

    @GetMapping
    public String showAllCardsByCurrentUser(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            List<Card> cards = cardService.getAllCardsByCurrentUser(user);
            model.addAttribute("cards", cards);
            model.addAttribute("userId", user.getId());
            model.addAttribute("currentUser", user);
            return "cards";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{cardId}")
    public String showSingleCard(@PathVariable int cardId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Card card = cardService.getCardById(cardId, user);
            model.addAttribute("card", card);
            model.addAttribute("cardId", cardId);
            model.addAttribute("currentUser", user);
            return "CardView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/wallets/{walletId}/add")
    public String showAddCardToWalletPage(@PathVariable int walletId,
                                          Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            walletService.getWalletById(walletId, user.getId());
            model.addAttribute("card", new CardDto());
            model.addAttribute("walletId", walletId);
            model.addAttribute("currentUser", user);
            return "card-add-new";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/wallets/{walletId}/add")
    public String addCardToWallet(@PathVariable int walletId,
                                  @Valid @ModelAttribute("card") CardDto cardDto,
                                  BindingResult bindingResult,
                                  Model model,
                                  HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "card-add-new";
        }

        try {
            Card cardToAdd = cardMapper.fromDto(cardDto, user);
            cardService.addCard(cardToAdd, walletId, user);
            return "redirect:/cards";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (CardMismatchException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{cardId}/update")
    public String showUpdateCardToWalletPage(@PathVariable int cardId,
                                             Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Card existingCard = cardService.getCardById(cardId, user);
            CardDto cardDto = cardMapper.toDto(existingCard);
            model.addAttribute("card", cardDto);
            model.addAttribute("currentUser", user);
            return "card-update";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{cardId}/update")
    public String updateCardToWallet(@PathVariable int cardId,
                                     @Valid @ModelAttribute("card") CardDto cardDto,
                                     BindingResult bindingResult,
                                     Model model,
                                     HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "card-update";
        }

        try {
            Card cardToUpdate = cardMapper.fromDto(cardId, cardDto, user);
            cardService.updateCard(cardToUpdate, user);
            return "redirect:/cards";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (CardMismatchException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{cardId}/delete")
    public String deleteCard(@PathVariable int cardId,
                             Model model, HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Card cardToDelete = cardService.getCardById(cardId, user);
            cardService.deleteCard(cardToDelete.getId(), user);
            return "redirect:/cards";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
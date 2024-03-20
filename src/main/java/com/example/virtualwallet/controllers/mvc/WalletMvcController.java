package com.example.virtualwallet.controllers.mvc;


import com.example.virtualwallet.config.ExternalApiUrlConfig;
import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.CardMapper;
import com.example.virtualwallet.helpers.TransferMapper;
import com.example.virtualwallet.helpers.WalletMapper;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransferRequestDto;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.services.contracts.CardService;
import com.example.virtualwallet.services.contracts.PaymentManager;
import com.example.virtualwallet.services.contracts.UserService;
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
@RequestMapping("/wallets")
public class WalletMvcController {

    private final WalletService walletService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final WalletMapper walletMapper;
    private final CardService cardService;
    private final TransferMapper transferMapper;
    private final ExternalApiUrlConfig externalApiUrlConfig;
    private final PaymentManager paymentManager;
    private int requestsLeft = 2;
    private final CardMapper cardMapper;

    @Autowired
    public WalletMvcController(WalletService walletService, UserService userService,
                               AuthenticationHelper authenticationHelper, WalletMapper walletMapper,
                               CardService cardService, TransferMapper transferMapper, ExternalApiUrlConfig externalApiUrlConfig, PaymentManager paymentManager, CardMapper cardMapper) {
        this.walletService = walletService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.walletMapper = walletMapper;
        this.cardService = cardService;
        this.transferMapper = transferMapper;
        this.externalApiUrlConfig = externalApiUrlConfig;
        this.paymentManager = paymentManager;
        this.cardMapper = cardMapper;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String showAllWallets(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);

            if (user != null) {
                model.addAttribute("currentUser", user);
            }

            List<Wallet> wallets = walletService.getAllWalletsByUserId(user);
            model.addAttribute("wallets", wallets);
            return "wallets";
        } catch (AuthorizationException e) {
            return "wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{walletId}")
    public String showSingleWallet(@PathVariable int walletId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            model.addAttribute("wallet", wallet);
            model.addAttribute("walletId", walletId);
            model.addAttribute("currentUser", user);
            return "wallet";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/new")
    public String showNewWalletPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        model.addAttribute("wallet", new WalletDto());
        model.addAttribute("currentUser", user);
        return "wallet-add";
    }

    @PostMapping("/new")
    public String createWallet(@Valid @ModelAttribute("wallet") WalletDto walletDto,
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
            model.addAttribute("currentUser", user);
            return "wallet-add";
        }

        try {
            Wallet newWallet = walletMapper.fromDto(walletDto, user);
            walletService.create(newWallet, user);
            return "redirect:/wallets";
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
        }
    }

    @GetMapping("/{walletId}/makeWalletDefault")
    public String showEditWalletPage(@PathVariable int walletId,
                                     Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet walletToUpdate = walletService.getWalletById(walletId, user.getId());
            WalletDto walletDto = walletMapper.toDto(walletToUpdate);


            model.addAttribute("walletId", walletId);
            model.addAttribute("wallet", walletDto);
            model.addAttribute("currentUser", user);
            return "wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{walletId}/makeWalletDefault")
    public String updateWallet(@PathVariable int walletId,
                               Model model,
                               HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet walletToUpdate = walletService.getWalletById(walletId, user.getId());
            walletService.update(walletToUpdate, user);
            return "redirect:/wallets";
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
        }
    }

    @GetMapping("/{walletId}/delete")
    public String showDeletePage(@PathVariable int walletId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet walletToDelete = walletService.getWalletById(walletId, user.getId());
            model.addAttribute("walletId", walletId);
            walletService.delete(walletToDelete, user);
            return "redirect:/wallets";
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

    @PostMapping("/{walletId}/delete")
    public String deleteWallet(@PathVariable int walletId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet walletToDelete = walletService.getWalletById(walletId, user.getId());
            walletService.delete(walletToDelete, user);
            return "redirect:/wallets";
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

    @GetMapping("/{walletId}/users")
    public String showUsersWalletPage(@PathVariable("walletId") int walletId,
                                      Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            model.addAttribute("wallet", wallet);
            model.addAttribute("currentUser", user);
            return "wallet-add-user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{walletId}/users")
    public String addUserToWallet(@PathVariable("walletId") int walletId,
                                  @ModelAttribute("userToAdd") String username,
                                  Model model,
                                  BindingResult bindingResult,
                                  HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "wallet-add-user";
        }

        try {
            User userToAdd = userService.getByUsername(username);
            walletService.addUsersToWallet(walletId, userToAdd.getId(), user);
            return "redirect:/wallets/" + walletId;
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
        }
    }

    @GetMapping("/{walletId}/users/delete")
    public String showDeleteUsersWalletPage(@PathVariable("walletId") int walletId,
                                            Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            model.addAttribute("wallet", wallet);
            model.addAttribute("currentUser", user);
            return "wallet-remove-user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{walletId}/users/delete")
    public String removeUserFromWallet(@PathVariable int walletId,
                                       @ModelAttribute("userToRemove") String username,
                                       Model model,
                                       BindingResult bindingResult,
                                       HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
        if (bindingResult.hasErrors()) {
            return "wallet-remove-user";
        }

        try {
            Wallet currentWallet = walletService.getWalletById(walletId, user.getId());
            User userToRemove = userService.getByUsername(username);
            walletService.removeUsersFromWallet(currentWallet.getId(), userToRemove.getId(), user);
            return "redirect:/wallets/" + walletId;
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

    @GetMapping("/add-money")
    public String getAddMoneyFromCardTransfer(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);
            List<Card> cards = cardService.getAllCardsByCurrentUser(user);
            List<Wallet> wallets = walletService.getAllWalletsByUserId(user);
//            Card card = cardService.getCardByCardNumber(recipientContactInfo);

//            model.addAttribute("recipientUser", recipientUser);
            model.addAttribute("user", user);
            model.addAttribute("cards", cards);
            model.addAttribute("wallets", wallets);
            model.addAttribute("transferValue", new TransferRequestDto());
            return "transaction-create-funding";
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/add-money")
    public String addMoneyFromCardTransfer(Model model, HttpSession session,
                                           @Valid @ModelAttribute("transferValue") TransferRequestDto transferRequestDto,
                                           BindingResult result) {
        if (result.hasErrors()) {
            return ("transaction-create-funding");
        } else {
            try {
                User user = authenticationHelper.tryGetCurrentUser(session);
                Wallet wallet = walletService.getWalletById(transferRequestDto.getReceiverWalletId(), user.getId());
                Card card = cardService.getCardById(transferRequestDto.getCardId(), user);
                Transfer transfer = transferMapper.fromDto(transferRequestDto, user, card);
                String response = walletService.moneyFromCardToWallet(transfer, wallet, user, card);

                if (response.equals("COMPLETED")) {
                    return "transaction-funding-completed";
                } else {
                    return "transaction-funding-rejected";
                }
            } catch (AuthorizationException e) {
                return "redirect:/auth/login";
            }
        }

//    @PostMapping("/add-money")
//    public String addMoneyFromCardTransfer(Model model, HttpSession session,
//                                           @Valid @ModelAttribute("cardDto") CardDto cardDto,
//                                           BindingResult cardBindingResult,
//                                           @Valid @ModelAttribute("transferValue") TransferRequestDto transferRequestDto,
//                                           BindingResult valueBindingResult) {
//        User user;
//        try {
//            user = authenticationHelper.tryGetCurrentUser(session);
//        } catch (AuthorizationException e) {
//            return "redirect:/auth/login";
//        }
//
//        Wallet receiverWallet = walletService.getWalletById(transferRequestDto.getReceiverWalletId(), user.getId());
//
//        if (!walletService.getAllWalletsByUserId(user).contains(walletService.getWalletById(receiverWallet.getId(), user.getId()))) {
//            model.addAttribute("error", WRONG_WALLET_ACCESS);
//            return "error";
//        }
//
//        if (valueBindingResult.hasErrors()) return "error";
//
//        String[] cardInfo = cardDto.getCardNumber().split(",");
//
//        String cardNumber = cardInfo[0];
//        String checkNumber = cardInfo[1];
//        cardDto.setCardNumber(cardNumber);
//        cardDto.setCheckNumber(checkNumber);
//
//        Card selectedCard = cardService.getCardByCardNumber(cardNumber);
//        int cardCurrentCvv = Integer.parseInt(checkNumber);
//
//        if (cardCurrentCvv != Integer.parseInt(checkNumber)) {
//            model.addAttribute("userWallet", walletService.getWalletById(receiverWallet.getId(), user.getId()));
//            cardBindingResult.rejectValue("cvv", "cvv.mismatch", "Wrong CVV");
//            return "error";
//        } else if (!selectedCard.getCardHolder().equals(user.getFirstName() + " " + user.getLastName())) {
//            cardBindingResult.rejectValue("user", "user.mismatch", "Wrong user");
//            return "error";
//        } else if (selectedCard.getExpirationDate().isBefore(LocalDate.now())) {
//            cardBindingResult.rejectValue("date", "date.expiration", "Expired card");
//            return "error";
//        }
//
//        MockBankDto mockBankDto = transferMapper.toDto(cardDto, transferRequestDto);
//        String url = externalApiUrlConfig.getMockBankUrl();
//
//        ResponseEntity<String> response = null;
//
//        try {
//            response = createExternalTransferRequestQuery(mockBankDto, url);
//        } catch (HttpClientErrorException e) {
//
//            if (e.getStatusCode().value() == 408) {
//                if (requestsLeft > 0) {
//                    --requestsLeft;
//                    addMoneyFromCardTransfer(model, session, cardDto, cardBindingResult,
//                            transferRequestDto, valueBindingResult);
//                } else {
//                    requestsLeft = 2;
//                    model.addAttribute("error", REQUEST_HTTP_STATUS_ERROR);
//                }
//            } else {
//                model.addAttribute("error", GENERAL_HTTP_STATUS_ERROR);
//                requestsLeft = 2;
//                return "error";
//            }
//
//            requestsLeft = 2;
//
//            try {
//                Transfer transfer;
//
//                switch (Objects.requireNonNull(response).getStatusCode().value()) {
//                    case 202:
//                        transfer = transferMapper.fromDtoMoney(transferRequestDto, Status.PENDING, receiverWallet.getId(), selectedCard, user);
//                        paymentManager.setCardPaymentIn(receiverWallet.getId(), transfer, user);
//                        return "redirect:/wallets";
//                    case 200:
//                        switch (Objects.requireNonNull(response.getBody()).toUpperCase()) {
//                            case "COMPLETED":
//                                transfer = transferMapper.fromDtoMoney(transferRequestDto, Status.COMPLETED, receiverWallet.getId(), selectedCard, user);
//                                paymentManager.setCardPaymentIn(receiverWallet.getId(), transfer, user);
//                                return "transaction-funding-completed";
//                            case "REJECT":
//                                transfer = transferMapper.fromDtoMoney(transferRequestDto, Status.REJECT, receiverWallet.getId(), selectedCard, user);
//                                paymentManager.setCardPaymentIn(receiverWallet.getId(), transfer, user);
//                                return "error";
//                        }
//                    default:
//                        model.addAttribute("error", UNKNOWN_HTTP_STATUS_ERROR);
//                        return "error";
//                }
//            } catch (AuthorizationException ex) {
//                return "error";
//            }
//        }
//
//        return "error";
//    }
    }
}
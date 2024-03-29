package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.TransactionFilterDto;
import com.example.virtualwallet.models.dtos.UserFilterDto;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import com.example.virtualwallet.utils.UserFilterOptions;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final TransactionService transactionService;

    public AdminMvcController(UserService userService, AuthenticationHelper authenticationHelper,
                              TransactionService transactionService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.transactionService = transactionService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/users")
    public String showAllUsers(@ModelAttribute("userFilterOptions") @Valid UserFilterDto filterDto,
                               @RequestParam(name = "contactType", defaultValue = "") String recipientContactType,
                               @RequestParam(name = "contact", defaultValue = "") String recipientContactInfo,
                               BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin-users";
        }

        UserFilterOptions userFilterOptions = new UserFilterOptions(
                filterDto.getUsername(),
                filterDto.getFirstName(),
                filterDto.getLastName(),
                filterDto.getEmail(),
                filterDto.getPhoneNumber(),
                filterDto.getRole(),
                filterDto.getStatus(),
                filterDto.getSortBy(),
                filterDto.getSortOrder());

        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (!recipientContactInfo.isEmpty()) {
            User recipientUser = userService.getByContact(recipientContactInfo);
            model.addAttribute("recipientUser", recipientUser);
        }

        List<User> users = userService.getAll(userFilterOptions);
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        return "admin-users";
    }

    @GetMapping("/transactions")
    public String showTransactions(@ModelAttribute("transactionFilterOptions") @Valid TransactionFilterDto filterDto,
                                   BindingResult bindingResult,
                                   HttpSession session,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "admin-transactions";
        }

        TransactionFilterOptions transactionFilterOptions = new TransactionFilterOptions(
                filterDto.getSender(),
                filterDto.getRecipient(),
                filterDto.getAmount(),
                filterDto.getCurrency(),
                filterDto.getDate(),
                filterDto.getSortBy(),
                filterDto.getSortOrder());

        try {
            authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            List<Transaction> transactions = transactionService.getAllTransactions(transactionFilterOptions);
            model.addAttribute("transactions", transactions);
            model.addAttribute("sender", filterDto.getSender());
            model.addAttribute("recipient", filterDto.getRecipient());
            model.addAttribute("amount", filterDto.getAmount());
            model.addAttribute("date", filterDto.getDate());
            model.addAttribute("filterOptions", filterDto);
            return "admin-transactions";
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

    @GetMapping("/users/{id}/block")
    public String showBlockUserPage(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeBlocked = userService.getById(id);
            userService.blockUser(currentUser, userToBeBlocked);
            return "redirect:/admin/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DeletionRestrictedException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/users/{id}/block")
    public String blockUser(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeBlocked = userService.getById(id);
            userService.blockUser(currentUser, userToBeBlocked);
            return "redirect:/admin/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DeletionRestrictedException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/users/{id}/unblock")
    public String showUnblockUserPage(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeUnblocked = userService.getById(id);
            userService.unBlockUser(currentUser, userToBeUnblocked);
            return "redirect:/admin/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/users/{id}/unblock")
    public String unBlockUser(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeUnblocked = userService.getById(id);
            userService.unBlockUser(currentUser, userToBeUnblocked);
            return "redirect:/admin/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/users/{id}/confirm-registration")
    public String showConfirmUserRegistration(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeConfirmed = userService.getById(id);
            userService.confirmUserRegistration(currentUser, userToBeConfirmed);
            return "redirect:/admin/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (IdentityNotVerifiedException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/users/{id}/confirm-registration")
    public String confirmUserRegistration(@PathVariable int id, @ModelAttribute("user") User user,
                                          BindingResult bindingResult, HttpSession session, Model model) {
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            return "admin-users";
        }

        try {
            User userToBeConfirmed = userService.getById(id);
            userService.confirmUserRegistration(user, userToBeConfirmed);
            return "redirect:/admin/users";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (DuplicateEntityException | IdentityNotVerifiedException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
package com.example.virtualwallet.controllers.mvc;


import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.WalletMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.WalletDto;
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

    @Autowired
    public WalletMvcController(WalletService walletService, UserService userService, AuthenticationHelper authenticationHelper, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.walletMapper = walletMapper;
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

            List<Wallet> wallets = walletService.getAll(user);
            model.addAttribute("wallets", wallets);
            return "WalletsView";
        } catch (AuthorizationException e) {
            return "WalletsView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
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
            return "WalletView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
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
        return "WalletCreateView";
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
            return "WalletCreateView";
        }

        try {
            Wallet newWallet = walletMapper.fromDto(walletDto, user);
            walletService.create(newWallet, user);
            return "redirect:/wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{walletId}/update")
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
            return "WalletUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{walletId}/update")
    public String updateWallet(@PathVariable int walletId,
                               @Valid @ModelAttribute("wallet") WalletDto walletDto,
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
            return "WalletUpdateView";
        }

        try {
            Wallet walletToUpdate = walletMapper.fromDto(walletDto, user);
            walletService.update(walletToUpdate, user);
            return "redirect:/wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
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
            walletService.delete(walletToDelete.getId(), user);
            return "redirect:/wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
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
            walletService.delete(walletId, user);
            return "redirect:/wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{walletId}/users/{userId}")
    public String showUsersWalletPage(@PathVariable("walletId") int walletId,
                                      @PathVariable("userId") int userId,
                                      Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            User userToAdd = userService.getById(userId);
            model.addAttribute("wallet", wallet);
            model.addAttribute("user", userToAdd);
            model.addAttribute("currentUser", user);
            return "WalletView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{walletId}/users/{userId}")
    public String addUserToWallet(@PathVariable("walletId") int walletId,
                                  @PathVariable("userId") int userId,
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
            return "WalletView";
        }

        try {
            walletService.addUsersToWallet(walletId, userId, user);
            return "redirect:/wallets/" + walletId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{walletId}/users/{userId}/delete")
    public String removeUserFromWallet(@PathVariable int walletId,
                                       @PathVariable int userId,
                                       Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet currentWallet = walletService.getWalletById(walletId, user.getId());
            User userToRemove = userService.getById(userId);
            walletService.removeUsersFromWallet(currentWallet.getId(), userToRemove.getId(), user);
            return "redirect:/wallets";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
}
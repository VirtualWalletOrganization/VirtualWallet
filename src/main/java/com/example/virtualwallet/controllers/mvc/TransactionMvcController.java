package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.TransactionMapper;
import com.example.virtualwallet.models.RecurringTransaction;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.RecurringTransactionDto;
import com.example.virtualwallet.models.dtos.TransactionDto;
import com.example.virtualwallet.models.dtos.TransactionFilterDto;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import com.example.virtualwallet.utils.TransactionFilterOptions;
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
@RequestMapping("/transactions")
public class TransactionMvcController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final AuthenticationHelper authenticationHelper;
    private final RecurringTransactionService recurringTransactionService;
    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public TransactionMvcController(TransactionService transactionService,
                                    TransactionMapper transactionMapper,
                                    AuthenticationHelper authenticationHelper, RecurringTransactionService recurringTransactionService,
                                    UserService userService, WalletService walletService) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.authenticationHelper = authenticationHelper;
        this.recurringTransactionService = recurringTransactionService;
        this.userService = userService;
        this.walletService = walletService;
    }

    @GetMapping
    public String showAllTransactions(@ModelAttribute("filterOptions") TransactionFilterDto filterDto,
                                      Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);

            if (user != null) {
                model.addAttribute("currentUser", user);
            }

            TransactionFilterOptions filterOptions = new TransactionFilterOptions(
                    filterDto.getSender(),
                    filterDto.getRecipient(),
                    filterDto.getAmount(),
                    filterDto.getCurrency(),
                    filterDto.getDate(),
                    filterDto.getSortBy(),
                    filterDto.getSortOrder());

            List<Transaction> transactions = transactionService.getAllTransactions(filterOptions);


            model.addAttribute("filterOptions", filterDto);
            model.addAttribute("transactions", transactions);
            return "TransactionssView";
        } catch (AuthorizationException e) {
            return "TransactionsView";
        }
    }

    @GetMapping("/{id}")
    public String showSingleTransaction(@PathVariable int id, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Transaction transaction = transactionService.getTransactionById(id);

            model.addAttribute("transactionId", id);
            model.addAttribute("transaction", transaction);
            model.addAttribute("currentUser", user);
            return "TransactionView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/wallets/{walletId}")
    public String showAllTransactionsByWalletId(@PathVariable int walletId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            List<Transaction> transactions = transactionService.getAllTransactionsByWalletId(wallet);

            model.addAttribute("walletId", walletId);
            model.addAttribute("transactions", transactions);
            model.addAttribute("currentUser", user);
            return "TransactionsView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/wallets/{walletId}/send")
    public String showNewTransactionPage(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        model.addAttribute("transaction", new TransactionFilterDto());
        model.addAttribute("currentUser", user);
        return "TransactionCreateView";
    }

    @PostMapping("/wallets/{walletId}/send")
    public String createTransaction(@PathVariable int walletId,
                                    @Valid @ModelAttribute("transaction") TransactionDto transactionDto,
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
            return "TransactionCreateView";
        }

        try {
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getByUsername(transactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, user,
                    walletReceiver, userReceiver);
            model.addAttribute("transaction", transaction);
            return "redirect:/posts";
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

    @GetMapping("/{transactionId}")
    public String showCompleteRequestTransactionPage(Model model, HttpSession session,
                                                     @PathVariable int transactionId) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);

            model.addAttribute("transactionId", transactionId);
            model.addAttribute("transaction", transaction);
            model.addAttribute("currentUser", user);
            return "TransactionUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{transactionId}")
    public String completeRequestTransaction(@PathVariable int transactionId,
                                             Model model,
                                             HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            transactionService.updateTransaction(transaction, user);
            return "redirect:/transactions";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{transactionId}/delete")
    public String deleteTransactionPage(@PathVariable int transactionId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Transaction transactionToDelete = transactionService.getTransactionById(transactionId);
            transactionService.delete(transactionToDelete, user);
            return "redirect:/transactions";
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

    @GetMapping("/wallets/{walletId}/request")
    public String showRequestTransactionPage(Model model, HttpSession session,
                                             @PathVariable int walletId) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            model.addAttribute("transaction", new TransactionDto());
            model.addAttribute("wallet", wallet);
            model.addAttribute("currentUser", user);
            return "TransactionRequestView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/wallets/{walletId}/request")
    public String requestTransaction(@PathVariable int walletId,
                                     @RequestBody TransactionDto transactionDto,
                                     Model model,
                                     HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet walletReceiver = walletService.getDefaultWallet(user.getId());
            User userSender = userService.getByUsername(transactionDto.getReceiver());
            Wallet walletSender = walletService.getDefaultWallet(userSender.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, userSender,
                    walletReceiver, user);
            transactionService.requestMoney(transaction, walletReceiver, user);
            return "redirect:/transactions";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/wallets/{walletId}/recurring")
    public String showNewRecurringTransactionPage(Model model, HttpSession session,
                                                  @PathVariable int walletId) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet wallet = walletService.getWalletById(walletId, user.getId());
            model.addAttribute("transaction", new TransactionDto());
            model.addAttribute("wallet", wallet);
            model.addAttribute("currentUser", user);
            return "TransactionRequestView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/wallets/{walletId}/recurring")
    public String requestTransaction(@PathVariable int walletId,
                                     @RequestBody RecurringTransactionDto recurringTransactionDto,
                                     Model model,
                                     HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getByUsername(recurringTransactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            RecurringTransaction recurringTransaction = transactionMapper.fromDtoTransaction(recurringTransactionDto,
                    walletSender,
                    user, walletReceiver, userReceiver);
            recurringTransactionService.createRecurringTransaction(recurringTransaction,
                    walletSender, user, walletReceiver, userReceiver);
            return "redirect:/transactions";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/recurring/{recurringId}")
    public String showEditRecurringTransactionPage(Model model, HttpSession session,
                                                   @PathVariable int recurringId) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            RecurringTransaction transaction = recurringTransactionService.getRecurringTransactionById(recurringId);

            model.addAttribute("recurringId", recurringId);
            model.addAttribute("transaction", transaction);
            model.addAttribute("currentUser", user);
            return "TransactionUpdateView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/recurring/{recurringId}")
    public String updateRecurringTransaction(@PathVariable int recurringId,
                                             @RequestBody RecurringTransactionDto recurringTransactionDto,
                                             Model model,
                                             HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            RecurringTransaction existingRecurring = recurringTransactionService.getRecurringTransactionById(recurringId);
            RecurringTransaction recurringTransaction = transactionMapper
                    .fromDtoTransactionUpdate(recurringTransactionDto, existingRecurring);
            recurringTransactionService.updateRecurringTransaction(recurringTransaction, user);
            return "redirect:/transactions";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
}



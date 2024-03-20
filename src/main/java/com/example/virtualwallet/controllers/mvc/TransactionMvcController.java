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
import com.example.virtualwallet.models.dtos.TransactionHistoryDto;
import com.example.virtualwallet.models.enums.TransactionType;
import com.example.virtualwallet.services.contracts.RecurringTransactionService;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import com.example.virtualwallet.utils.TransactionFilterOptions;
import com.example.virtualwallet.utils.TransactionHistoryFilterOptions;
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
import java.util.Optional;

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

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
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

    @GetMapping("/user-history")
    public String showAllTransactionsByUserId(@ModelAttribute TransactionHistoryDto transactionHistoryDto,
                                              Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            TransactionHistoryFilterOptions filterOptions = new TransactionHistoryFilterOptions(
                    transactionHistoryDto.getStartDate(),
                    transactionHistoryDto.getEndDate(),
                    transactionHistoryDto.getCounterparty(),
                    transactionHistoryDto.getAmount(),
                    transactionHistoryDto.getSortBy(),
                    transactionHistoryDto.getSortOrder());
            List<Transaction> transactions = transactionService.getAllTransactionsByUserId(user.getId(), filterOptions);
            model.addAttribute("transactionHistoryDto", transactionHistoryDto);
            model.addAttribute("transactions", transactions);
            model.addAttribute("currentUser", user);
            return "transaction-history";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/wallets/{walletId}/confirm")
    public String showConfirmTransactionPage(@PathVariable int walletId,
                                             Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        model.addAttribute("transaction", new TransactionDto());
        model.addAttribute("walletId", walletId);
        model.addAttribute("currentUser", user);
        return "transaction-confirmation";
    }

    @PostMapping("/wallets/{walletId}/confirm")
    public String confirmTransactionPage(@PathVariable int walletId,
                                         @Valid @ModelAttribute TransactionDto transactionDto,
                                         BindingResult bindingResult,
                                         Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "transaction-confirmation";
        }

        try {
            Wallet walletSender = walletService.getWalletById(walletId, user.getId());
            User userReceiver = userService.getById(transactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, user,
                    walletReceiver, userReceiver);
            transactionService.confirmTransaction(transaction, walletSender, user);
            return "redirect:/transactions/wallets/{walletId}/send";
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

    @GetMapping("/send-money")
    public String showNewTransactionPage(
            @RequestParam(name = "contact", defaultValue = "") String recipientContactInfo,
            Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            if (!recipientContactInfo.isEmpty()) {
                User recipientUser = userService.getByContact(recipientContactInfo);
                List<Wallet> walletList = walletService.getAllWalletsByUserId(user);
                model.addAttribute("recipientUser", recipientUser);
                model.addAttribute("walletList", walletList);
            }

            model.addAttribute("transaction", new TransactionDto());
            model.addAttribute("recurringTransaction", new RecurringTransactionDto());
            model.addAttribute("currentUser", user);
            return "transaction-send-money";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/send-money")
    public String createTransaction(@Valid @ModelAttribute("transaction") TransactionDto transactionDto,
                                    BindingResult bindingResult,
                                    @Valid @ModelAttribute("recurringTransaction") RecurringTransactionDto recurringTransactionDto,
                                    BindingResult recurringBindingResult,
                                    Model model,
                                    HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors() || recurringBindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            return "transaction-send-money";
        }

        try {
            Wallet walletSender = walletService.getWalletById(transactionDto.getSenderWalletId(), user.getId());
            User userReceiver = userService.getById(transactionDto.getReceiver());
            Wallet walletReceiver = walletService.getDefaultWallet(userReceiver.getId());

            if (transactionDto.getTransactionType().equals(TransactionType.RECURRING.name())) {
                RecurringTransaction recurringTransaction = transactionMapper.fromDtoTransaction(recurringTransactionDto,
                        walletSender,
                        user, walletReceiver, userReceiver);
                recurringTransactionService.createRecurringTransaction(recurringTransaction,
                        walletSender, user, walletReceiver, userReceiver);
                return "transaction-completed-recurring";
            } else {
                Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, user,
                        walletReceiver, userReceiver);
                transactionService.createTransaction(transaction, walletSender, user, walletReceiver, userReceiver);
                return "transaction-completed";
            }
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
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    @GetMapping("/request-money")
    public String showRequestTransactionPage(
            @RequestParam(name = "contact", defaultValue = "") String senderContactInfo,
            Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            if (!senderContactInfo.isEmpty()) {
                User senderUser = userService.getByContact(senderContactInfo);
                List<Wallet> walletList = walletService.getAllWalletsByUserId(user);
                model.addAttribute("senderUser", senderUser);
                model.addAttribute("walletList", walletList);
            }

            model.addAttribute("transaction", new TransactionDto());
            model.addAttribute("currentUser", user);
            return "transaction-request-money";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/request-money")
    public String requestTransaction(@Valid @ModelAttribute("transaction") TransactionDto transactionDto,
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
            User userSender = userService.getById(transactionDto.getReceiver());
            Wallet walletSender = walletService.getDefaultWallet(userSender.getId());
            Transaction transaction = transactionMapper.fromDtoMoney(transactionDto, walletSender, userSender,
                    walletReceiver, user);
            transactionService.requestMoney(transaction, walletReceiver, user);
            return "transaction-requested";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/recurring-history")
    public String showAllTransactionsByUserId(@ModelAttribute RecurringTransactionDto recurringTransactionDto,
                                              Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Optional<List<Transaction>> requestedTransactions = transactionService.getAllTransactionsByTransactionType(user);
            requestedTransactions.ifPresent(transactions -> model.addAttribute("transactions", transactions));
            model.addAttribute("recurringTransactionDto", recurringTransactionDto);
            model.addAttribute("currentUser", user);
            return "recurring-transaction-history";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/recurring")
    public String showAllRecurringTransactions(Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Optional<List<RecurringTransaction>> recurringTransactions = recurringTransactionService.getRecurringTransactionByUserId(user.getId());
            recurringTransactions.ifPresent(transactions -> model.addAttribute("transactions", recurringTransactions.get()));
            model.addAttribute("currentUser", user);
            return "recurring-transaction";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/recurring/{recurringId}/update")
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
            return "recurring-transaction-update";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/recurring/{recurringId}/update")
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
            return "redirect:/transactions/recurring";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/recurring/{recurringId}/cancel")
    public String cancelRecurringTransaction(@PathVariable int recurringId,
                                             Model model,
                                             HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            RecurringTransaction recurringTransaction = recurringTransactionService.getRecurringTransactionById(recurringId);
            recurringTransactionService.cancelRecurringTransaction(recurringTransaction, user);
            return "redirect:/transactions/recurring";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/notifications")

    public String showAllNotifications(HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Optional<List<Transaction>> requestedTransactions = transactionService.getAllTransactionsByStatus(user);
            requestedTransactions.ifPresent(transactions -> model.addAttribute("transactionList", transactions));
            model.addAttribute("user", user);
            return "notifications";
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

    @PostMapping("/{transactionId}/complete")
    public String completeRequestTransactionPage(Model model, HttpSession session,
                                                 @PathVariable int transactionId) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            model.addAttribute("transaction", transaction);
            model.addAttribute("currentUser", user);
            transactionService.updateTransaction(transaction, user);
            return "redirect:/transactions/notifications";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/{transactionId}/reject")
    public String rejectRequestTransactionPage(Model model, HttpSession session,
                                               @PathVariable int transactionId) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            model.addAttribute("transaction", transaction);
            model.addAttribute("currentUser", user);
            transactionService.delete(transaction, user);
            return "redirect:/transactions/notifications";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (InsufficientBalanceException e) {
            model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (AuthorizationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}
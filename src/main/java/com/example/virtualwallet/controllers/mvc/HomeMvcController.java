package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final TransactionService transactionService;

    public HomeMvcController(AuthenticationHelper authenticationHelper,
                             UserService userService,
                             TransactionService transactionService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping
    public String showHomePage(Model model, HttpSession session) {
        long userNum = userService.getAllNumber();

        model.addAttribute("userNumber", userNum);

        try {
            User user = authenticationHelper.tryGetCurrentUser(session);

            if (user != null) {
                model.addAttribute("currentUser", user);
                Optional<List<Transaction> > transactionList=transactionService.getAllTransactionsByStatus(user);
                transactionList.ifPresent(transactions -> model.addAttribute("transactionList", transactions));
            }

            return "index";
        } catch (AuthorizationException e) {
            return "index";
        }
    }

//    @GetMapping("/about")
//    public String showAboutPage(Model model, HttpSession session) {
//        try {
//            User user = authenticationHelper.tryGetCurrentUser(session);
//
//            if (user != null) {
//                model.addAttribute("currentUser", user);
//            }
//
//            return "AboutView";
//        } catch (AuthorizationException e) {
//            return "AboutView";
//        }
//    }
}
package com.example.virtualwallet.controllers.mvc;


import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMvcController {

    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;

    @Autowired
    public HomeMvcController(AuthenticationHelper authenticationHelper, UserService userService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
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
            }

            return "HomeView";
        } catch (AuthorizationException e) {
            return "HomeView";
        }
    }

    @GetMapping("/about")
    public String showAboutPage(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetCurrentUser(session);

            if (user != null) {
                model.addAttribute("currentUser", user);
            }

            return "AboutView";
        } catch (AuthorizationException e) {
            return "AboutView";
        }
    }
}
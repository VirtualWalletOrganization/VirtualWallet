package com.example.virtualwallet.controllers.mvc;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DeletionRestrictedException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.UserFilterDto;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.utils.UserFilterOptions;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.virtualwallet.utils.Messages.UNAUTHORIZED_USER_ERROR_MESSAGE;

@Controller
@RequestMapping("/admin")
public class AdminMvcController {

    private UserService userService;
    private AuthenticationHelper authenticationHelper;

    public AdminMvcController(UserService userService, AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showAdminPortal(@ModelAttribute("userFilterOptions") UserFilterDto filterDto,
                                  HttpSession session, Model model) {
        UserFilterOptions userFilterOptions = new UserFilterOptions(
                filterDto.getUsername(),
                filterDto.getFirstName(),
                filterDto.getLastName(),
                filterDto.getEmail(),
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

        try {
            if (user.getUsersRole().getRole().equals(Role.ADMIN)) {
                throw new AuthorizationException(UNAUTHORIZED_USER_ERROR_MESSAGE);
            }

            List<User> users = userService.getAll(user, userFilterOptions);
            model.addAttribute("users", users);
            model.addAttribute("user", user);
            model.addAttribute("currentUser", user);
            model.addAttribute("filterOptions", filterDto);
            model.addAttribute("isAuthenticated", true);
            return "admin-users";
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

//    @GetMapping("/users")
//    public ModelAndView showListOfUsers(ModelAndView modelAndView,
//                                        @RequestParam(defaultValue = "1") int page,
//                                        @RequestParam(defaultValue = "5") int pageSize,
//                                        @RequestParam(name = "filterType", defaultValue = DEFAULT_EMPTY_VALUE, required = false) String contactType,
//                                        @RequestParam(name = "contact", defaultValue = DEFAULT_EMPTY_VALUE, required = false) String contactInformation) {
//        try {
//            PaginatedUserListDto paginatedUserListDto =
//                    dtoListsMediatorService.getPresentableUsersWithPagination(contactType, contactInformation, page, pageSize);
//            prepareModelAndViewService.forGettingAdminUserList(modelAndView, paginatedUserListDto, contactType, contactInformation);
//        } catch (IllegalArgumentException e) {
//            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
//            modelAndView.addObject("invalidValue", e.getMessage());
//        }
//        modelAndView.setViewName("admin-users");
//        return modelAndView;
//    }
//
//    @GetMapping("/transactions")
//    public ModelAndView showListOfTransactions(@RequestParam(required = false) Date startDate,
//                                               @RequestParam(required = false) Date endDate,
//                                               @RequestParam(name = "sender", required = false) String senderUsername,
//                                               @RequestParam(name = "recipient", required = false) String recipientUsername,
//                                               @RequestParam(name = "amount", defaultValue = DEFAULT_EMPTY_VALUE) String amount,
//                                               @RequestParam(name = "date", defaultValue = DEFAULT_EMPTY_VALUE) String date,
//                                               @RequestParam(defaultValue = "1") int page,
//                                               @RequestParam(defaultValue = "10") int pageSize,
//                                               ModelAndView modelAndView) {
//        try {
//            PaginatedTransactionListDtoForAdmin list = dtoListsMediatorService.getPresentableTransactionsForAdminWithPagination(startDate,
//                    endDate, senderUsername, recipientUsername, amount, date, page, pageSize);
//            prepareModelAndViewService.forGettingAdminTransactionList(modelAndView, list, senderUsername, recipientUsername, amount, date);
//        } catch (IllegalArgumentException e) {
//            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
//            prepareModelAndViewService.forHandlingErrorWithPresentableTransactions(modelAndView, e.getMessage());
//        } catch (EntityNotFoundException e) {
//            modelAndView.setStatus(HttpStatus.NOT_FOUND);
//            prepareModelAndViewService.forHandlingErrorWithPresentableTransactions(modelAndView, e.getMessage());
//        }
//        modelAndView.setViewName("admin-transactions");
//        return modelAndView;
//    }

    @GetMapping("/{id}/update-to-admin")
    public String updateToAdminForm(@PathVariable int id, Model model,
                                    HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            User userToUpdate = userService.getById(id);
            userService.updateToAdmin(userToUpdate, user);
            return "redirect:/admin";
        } catch (jakarta.persistence.EntityNotFoundException e) {
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

    @PostMapping("/{id}/update-to-admin")
    public String updateToAdmin(@PathVariable int id,
                                Model model,
                                HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            User userToUpdate = userService.getById(id);
            userService.updateToAdmin(userToUpdate, user);
            return "redirect:/admin";
        } catch (jakarta.persistence.EntityNotFoundException e) {
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

    @GetMapping("/{id}/update-to-user")
    public String updateToUserForm(@PathVariable int id, Model model,
                                   HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            User userToUpdate = userService.getById(id);
            userService.updateToUser(userToUpdate, user);
            return "redirect:/admin";
        } catch (jakarta.persistence.EntityNotFoundException e) {
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
        } catch (DeletionRestrictedException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/{id}/update-to-user")
    public String updateToUser(@PathVariable int id,
                               Model model,
                               HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetCurrentUser(session);
        } catch (AuthorizationException e) {
            return "redirect:/auth/login";
        }

        try {
            User userToUpdate = userService.getById(id);
            userService.updateToUser(userToUpdate, user);
            return "redirect:/admin";
        } catch (jakarta.persistence.EntityNotFoundException e) {
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
        } catch (DeletionRestrictedException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/block")
    public String showBlockUserPage(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeBlocked = userService.getById(id);
            userService.blockUser(currentUser, userToBeBlocked);
            return "redirect:/admin";
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

    @PostMapping("/{id}/block")
    public String blockUser(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeBlocked = userService.getById(id);
            userService.blockUser(currentUser, userToBeBlocked);
            return "redirect:/admin";
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

    @GetMapping("/{id}/unblock")
    public String showUnblockUserPage(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeUnblocked = userService.getById(id);
            userService.unBlockUser(currentUser, userToBeUnblocked);
            return "redirect:/admin";
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

    @PostMapping("/{id}/unblock")
    public String unBlockUser(@PathVariable int id, HttpSession session, Model model) {
        try {
            User currentUser = authenticationHelper.tryGetCurrentUser(session);
            User userToBeUnblocked = userService.getById(id);
            userService.unBlockUser(currentUser, userToBeUnblocked);
            return "redirect:/admin";
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
}
package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityAlreadyDeleteException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.UserMapper;
import com.example.virtualwallet.helpers.WalletMapper;
import com.example.virtualwallet.models.Photo;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.*;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import com.example.virtualwallet.utils.UserFilterOptions;
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
import java.util.stream.Collectors;

import static com.example.virtualwallet.utils.Messages.UNAUTHORIZED_USER_ERROR_MESSAGE;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;
    private final WalletMapper walletMapper;
    private final WalletService walletService;

    @Autowired
    public UserRestController(UserService userService,
                              AuthenticationHelper authenticationHelper,
                              UserMapper userMapper,
                              WalletMapper walletMapper,
                              WalletService walletService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
        this.walletMapper = walletMapper;
        this.walletService = walletService;
    }

    @GetMapping
    @Operation(tags ={"Get all users"},
            summary = "This method retrieve information about all users.",
            description = "This method search for all users in the data base. When a person is authorized and there are registered users, a list with all users will be presented.",
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "User(s) was/were found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to access the list of users."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User(s) is/were not found.")})
    public List<UserResponseDto> getAll(@RequestHeader HttpHeaders headers,
                                        @RequestParam(required = false) String username,
                                        @RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String phoneNumber,
                                        @RequestParam(required = false) Role role,
                                        @RequestParam(required = false) UserStatus status,
                                        @RequestParam(required = false) String sortBy,
                                        @RequestParam(required = false) String sortOrder) {
        try {
            authenticationHelper.tryGetUser(headers);
            UserFilterOptions userFilterOptions =
                    new UserFilterOptions(
                            username, firstName, lastName, email, phoneNumber, role, status, sortBy, sortOrder);

            List<User> users = userService.getAll(userFilterOptions);

            return users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }

    }

    @GetMapping("/{id}")
    @Operation(tags ={"Get a user"},
            operationId = "Id to be searched for",
            summary = "This method search for a user when id is given.",
            description = "This method search for a user. A valid id must be given as an input.",
            parameters = {@Parameter( name = "id", description = "path variable", example = "5")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to access this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found.")})
    public UserResponseDto getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getById(id);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/administrative-search", params = {"username"})
    @Operation(tags ={"Search for a user"},
            summary = "This method search for a user when user name is given.",
            description = "This method search for a user. A valid user name must be given as an input.",
            parameters = {@Parameter( name = "username", description = "Username", example = "yoana")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to search for this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this title was not found.")})
    public UserResponseDto getByUsernameA(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        try {
            User ifAdmin = authenticationHelper.tryGetUser(headers);
            if (ifAdmin.getUsersRole().getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
            }
            User targetUser = userService.getByUsername(username);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/administrative-search", params = {"email"})
    @Operation(tags ={"Search for a user"},
            summary = "This method search for a user when user's email is given.",
            description = "This method search for a user. A valid user email must be given as an input.",
            parameters = {@Parameter( name = "email", description = "User's email address", example = "yoana@abv.bg")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to search for this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this title was not found.")})
    public UserResponseDto getByEmailA(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            User ifAdmin = authenticationHelper.tryGetUser(headers);
            if (ifAdmin.getUsersRole().getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
            }
            User targetUser = userService.getByEmail(email);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/administrative-search", params = {"phoneNumber"})
    @Operation(tags ={"Search for a user"},
            summary = "This method search for a user when user's phone number is given.",
            description = "This method search for a user. A valid user phone number must be given as an input.",
            parameters = {@Parameter( name = "phoneNumber", description = "User's phone number", example = "0898252771")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to search for this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this title was not found.")})
    public UserResponseDto getByPhoneNumberA(@RequestHeader HttpHeaders headers, @RequestParam String phoneNumber) {
        try {
            User ifAdmin = authenticationHelper.tryGetUser(headers);
            if (ifAdmin.getUsersRole().getRole() != Role.ADMIN) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
            }
            User targetUser = userService.getByPhoneNumber(phoneNumber);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @PostMapping
    @Operation(tags ={"Register user"},
            summary = "Using this method, a new user can be registered.",
            description = "This method do registering of a new user. When valid parameters are given as an input, a new user is being created.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts user dto object as a parameter.",
                    content = @Content(schema = @Schema(implementation = User.class))),
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been created successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "Not found.")})
    public UserResponseDto registerUser(@Valid @RequestBody RegisterDto userDto) {
        try {
            User user = userMapper.fromDtoRegister(userDto);
            Wallet wallet = walletMapper.fromDtoCreateWallet(userDto,user);
            Photo photo = userMapper.fromDtoCreatePhoto(userDto);

            userService.registerUser(user);
            user.setPhoto(photo);
            userService.createPhoto(photo, user);
            walletService.create(wallet, user);

            userMapper.toDtoRegisterAndUpdateUser(user);
            return userMapper.toDtoRegisterAndUpdateUser(user);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/confirm-registration")
    @Operation(tags ={"Confirm registration"},
            summary = "Using this method, a user's profile is being confirmed and its status is being changed.",
            description = "This method makes a user capable of using the app, because the user's registration is being confirmed. When valid id is given as an input and the performing user is with role Admin, the user will be updated.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been updated successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to update this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found")})
    public void confirmUserRegistration(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User admin = authenticationHelper.tryGetUser(headers);
            User toBeVerified = userService.getById(id);
            userService.confirmUserRegistration(admin, toBeVerified);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(tags ={"Update a user"},
            summary = "Using this method, a user is being updated.",
            description = "This method updates the fields of a user. When valid id and parameters are given as an input, the user is updated.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "This is a request body that accepts user dto object as a parameter.",
                    content = @Content(schema = @Schema(implementation = User.class))),
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been updated successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to update this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found")})
    public UserResponseDto updateUser(@RequestHeader HttpHeaders headers,
                                      @PathVariable int id, @Valid @RequestBody UpdateUserDto userDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeUpdated =userMapper.fromDtoUpdate(id, userDto);
            userService.updateUser(user, userToBeUpdated);
            return userMapper.toDtoRegisterAndUpdateUser(userToBeUpdated);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(tags ={"Delete a user"},
            summary = "Using this method, a user is being deleted.",
            description = "This method deletes a user. When valid id is given as an input, the user is deleted.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            responses ={@ApiResponse(responseCode = "410", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been deleted successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to delete this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found.")})
    public ResponseEntity<Void> deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.deleteUser(id, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityAlreadyDeleteException e) {
            throw new ResponseStatusException(HttpStatus.GONE, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/admins")
    @Operation(tags ={"Update a user to admin"},
            summary = "Using this method, a user is being updated to be an admin.",
            description = "This method change the role of a user to be an admin. When valid id is given as an input, the user is being updated.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been updated successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to update this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found.")})
    public UserResponseDto updateToAdmin(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeUpdate = userService.getById(id);
            userService.updateToAdmin(userToBeUpdate, user);
            return userMapper.toDto(userToBeUpdate);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/role/user")
    @Operation(tags ={"Update an admin to user"},
            summary = "Using this method, an admin is being updated to be an user.",
            description = "This method change the role of an admin to be an user. When valid id is given as an input, the user is being updated.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been updated successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to update this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "Admin with this id was not found.")})
    public ResponseEntity<Void> updateToUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getById(id);
            userService.updateToUser(targetUser, user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/block")
    @Operation(tags ={"Block a user"},
            summary = "Using this method, a user can be blocked.",
            description = "When a valid id is given and there is the proper authorization, a user'status can be changed to blocked.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been blocked successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to block this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found.")})
    public UserResponseDto blockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeBlock = userService.getById(id);
            userService.blockUser(user, userToBeBlock);
            return userMapper.toDto(userToBeBlock);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/unblock")
    @Operation(tags ={"Unblock a user"},
            summary = "Using this method, a user can be unblocked.",
            description = "When a valid id is given and there is the proper authorization, a user'status can be changed from blocked to unblocked.",
            parameters = {@Parameter( name = "id", description = "user's id", example = "1")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been unblocked successfully"),
                    @ApiResponse(responseCode = "409", content = @Content(schema = @Schema(implementation = User.class)), description = "There is a conflict."),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to unblock this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this id was not found.")})
    public UserResponseDto unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeUnblock = userService.getById(id);
            userService.unBlockUser(user, userToBeUnblock);
            return userMapper.toDto(userToBeUnblock);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping(value = "/search", params = {"username"})
    @Operation(tags ={"Search for a user"},
            summary = "This method search for a user when user name is given.",
            description = "This method search for a user. A valid user name must be given as an input.",
            parameters = {@Parameter( name = "username", description = "Username", example = "yoana")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to search for this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this title was not found.")})
    public ReturnDto getByUsername(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getByUsername(username);
            return userMapper.fromDtoShow(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/search", params = {"email"})
    @Operation(tags ={"Search for a user"},
            summary = "This method search for a user when user's email is given.",
            description = "This method search for a user. A valid user email must be given as an input.",
            parameters = {@Parameter( name = "email", description = "User's email address", example = "yoana@abv.bg")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to search for this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this title was not found.")})
    public ReturnDto getByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getByEmail(email);
            return userMapper.fromDtoShow(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/search", params = {"phoneNumber"})
    @Operation(tags ={"Search for a user"},
            summary = "This method search for a user when user's phone number is given.",
            description = "This method search for a user. A valid user phone number must be given as an input.",
            parameters = {@Parameter( name = "phoneNumber", description = "User's phone number", example = "0898252771")},
            responses ={@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)), description = "The user has been found successfully"),
                    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = User.class)), description = "You are not allowed to search for this user."),
                    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = User.class)), description = "User with this title was not found.")})
    public ReturnDto getByPhoneNumber(@RequestHeader HttpHeaders headers, @RequestParam String phoneNumber) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getByPhoneNumber(phoneNumber);
            return userMapper.fromDtoShow(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

}
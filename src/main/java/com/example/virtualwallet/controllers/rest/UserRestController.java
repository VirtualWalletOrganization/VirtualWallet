package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityAlreadyDeleteException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.UserMapper;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.PhoneNumberDto;
import com.example.virtualwallet.models.dtos.UserDto;
import com.example.virtualwallet.models.dtos.UserResponseDto;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.utils.UserFilterOptions;
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

    @Autowired
    public UserRestController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserResponseDto> getAll(@RequestHeader HttpHeaders headers,
                                        @RequestParam(required = false) String username,
                                        @RequestParam(required = false) String firstName,
                                        @RequestParam(required = false) String lastName,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) Role role,
                                        @RequestParam(required = false) UserStatus status,
                                        @RequestParam(required = false) String sortBy,
                                        @RequestParam(required = false) String sortOrder) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            UserFilterOptions userFilterOptions =
                    new UserFilterOptions(
                            username, firstName, lastName, email, role, status, sortBy, sortOrder);

            List<User> users = userService.getAll(user, userFilterOptions);

            return users.stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping("/{id}")
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

    @GetMapping(value = "/search", params = {"username"})
    public UserResponseDto getByUsername(@RequestHeader HttpHeaders headers, @RequestParam String username) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getByUsername(username);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/search", params = {"email"})
    public UserResponseDto getByEmail(@RequestHeader HttpHeaders headers, @RequestParam String email) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getByEmail(email);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @GetMapping(value = "/search", params = {"phoneNumber"})
    public UserResponseDto getByPhoneNumber(@RequestHeader HttpHeaders headers, @RequestParam String phoneNumber) {
        try {
            authenticationHelper.tryGetUser(headers);
            User targetUser = userService.getByPhoneNumber(phoneNumber);
            return userMapper.toDto(targetUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, UNAUTHORIZED_USER_ERROR_MESSAGE);
        }
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDto userDto, @RequestBody WalletDto walletDto) {
        try {
            User user = userMapper.fromDtoRegister(userDto);
            userService.registerUser(user, walletDto);
            userMapper.toDtoRegisterAndUpdateUser(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{id}/confirm-registration")
    public ResponseEntity<User> confirmUserRegistration(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestBody User user) {
        try {
            authenticationHelper.tryGetUser(headers);
            User currentUser = userService.getById(id);
            User updatedUser = userService.confirmUserRegistration(currentUser, user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@RequestHeader HttpHeaders headers,
                                      @PathVariable int id, @Valid @RequestBody UserDto userDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeUpdated = userMapper.fromDtoUpdate(id, userDto);
            userService.updateUser(userToBeUpdated, user);
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

    @PutMapping("/{id}/profile-picture")
    public ResponseEntity<Void> saveProfilePictureUrl(@RequestHeader HttpHeaders headers, @PathVariable int id, @RequestParam String profilePictureUrl) {
        try {
            authenticationHelper.tryGetUser(headers);
            User currentUser = userService.getById(id);
            userService.saveProfilePictureUrl(currentUser.getUsername(), profilePictureUrl);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<String> getProfilePictureUrl(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            User currentUser = userService.getById(id);
            String profilePictureUrl = userService.getProfilePictureUrl(currentUser.getUsername());
            return new ResponseEntity<>(profilePictureUrl, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/{id}/admins")
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

    @PutMapping("/{id}/phone-number")
    public UserResponseDto updateUserPhoneNumber(@RequestHeader HttpHeaders headers,
                                                 @PathVariable int id,
                                                 @Valid @RequestBody PhoneNumberDto phoneNumberDto, UserDto dto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userPhoneNumberToBeUpdate = userMapper.fromDtoUpdatePhoneNumber(id, phoneNumberDto, dto);
            userService.addPhoneNumberToAdmin(user, userPhoneNumberToBeUpdate);
            return userMapper.toDto(userPhoneNumberToBeUpdate);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}/phone-number")
    public void deleteUserPhoneNumber(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.deletePhoneNumber(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityAlreadyDeleteException e) {
            throw new ResponseStatusException(HttpStatus.GONE, e.getMessage());
        }
    }
}
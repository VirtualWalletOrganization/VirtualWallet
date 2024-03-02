package com.example.virtualwallet.services;

import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.models.enums.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Optional;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityAlreadyDeleteException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.utils.UserFilterOptions;

import static com.example.virtualwallet.helpers.createMockUser;
import static com.example.virtualwallet.helpers.createMockUserFilterOptions;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepository mockRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    public void get_Should_CallRepository() {
        User user = createMockUser();
        UserFilterOptions mockUserFilterOptions = createMockUserFilterOptions();

        userService.getAll(user, mockUserFilterOptions);

        Mockito.verify(mockRepository, Mockito.times(1))
                .getAll(mockUserFilterOptions);
    }

    @Test
    public void getUserById_Should_ReturnUser_When_MatchExists() {
        User user = createMockUser();

        Mockito.when(mockRepository.getById(user.getId())).thenReturn(Optional.of(user));

        userService.getById(user.getId());

        Mockito.verify(mockRepository, Mockito.times(1)).getById(user.getId());
    }

    @Test
    public void getUserByUsername_Should_ReturnUser_When_MatchExists() {
        String username = "testUser";
        User user = new User();

        Mockito.when(mockRepository.getByUsername(username)).thenReturn(Optional.of(user));

        userService.getByUsername(username);

        Mockito.verify(mockRepository, Mockito.times(1)).getByUsername(username);
    }


    @Test
    public void registerUser_Should_ThrowException_When_UserWithSameNameExists() {
        User user = createMockUser();
        User existingUserWithTheSameName = createMockUser();
        existingUserWithTheSameName.setId(2);

        Mockito.when(mockRepository.getByUsername(user.getUsername()))
                .thenReturn(Optional.of(existingUserWithTheSameName));

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userService.registerUser(user, new WalletDto()));
    }


    @Test
    public void update_Should_CallRepository_When_UpdatingExistingUser() {
        User targetUser = createMockUser();
        User executingUser = createMockUser();

        userService.updateUser(targetUser, executingUser);

        Mockito.verify(mockRepository, Mockito.times(1))
                .updateUser(targetUser);
    }

    @Test
    public void update_Should_ThrowException_When_UsernameIsTaken() {
        User targetUser = createMockUser();
        User executingUser = createMockUser();
        executingUser.setUsername("DifferentUsername");

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateUser(targetUser, executingUser));
    }

    @Test
    public void delete_Should_ThrowException_When_UserAlreadyDeleted() {
        int userIdToDelete = 1;
        User executingUser = createMockUser();
        User userToDelete = createMockUser();
        userToDelete.setDeleted(true);

        Mockito.when(mockRepository.getById(userIdToDelete)).
                thenReturn(Optional.of(userToDelete));

        Assertions.assertThrows(
                EntityAlreadyDeleteException.class,
                () -> userService.deleteUser(userIdToDelete, executingUser));
    }

    @Test
    public void updateToAdmin_Should_CallRepository_When_UpdatingExistingUser() {
        User targetUser = createMockUser();
        targetUser.setRole(Role.USER);
        User executingUser = createMockUser();

        userService.updateToAdmin(targetUser, executingUser);

        Mockito.verify(mockRepository, Mockito.times(1))
                .updateUser(targetUser);
    }

    @Test
    public void updateToAdmin_Should_ThrowException_When_TargetUserIsAlreadyAdmin() {
        User targetUser = createMockUser();
        User executingUser = createMockUser();

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userService.updateToAdmin(targetUser, executingUser));
    }

    @Test
    public void blockUser_Should_ThrowException_When_UserAlreadyBlocked() {
        User admin = createMockUser();
        User blockUser = createMockUser();
        blockUser.setStatus(UserStatus.BLOCKED);

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userService.blockUser(admin, blockUser));
    }

    @Test
    public void unblockUser_Should_CallRepository_When_UnBlockUserExist() {
        User admin = createMockUser();
        User unBlockUser = createMockUser();
        unBlockUser.setStatus(UserStatus.BLOCKED);

        userService.unBlockUser(admin, unBlockUser);

        Mockito.verify(mockRepository, Mockito.times(1))
                .updateUser(unBlockUser);
    }

    @Test
    public void unblockUser_Should_ThrowException_When_UserAlreadyUnBlocked() {
        User admin = createMockUser();
        User blockUser = createMockUser();

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userService.unBlockUser(admin, blockUser));
    }

    @Test
    public void addPhoneNumberToAdmin_Should_CallRepository_When_PhoneNumberExist() {
        User admin = createMockUser();
        User userPhoneNumberToBeUpdate = createMockUser();
        userPhoneNumberToBeUpdate.setPhoneNumber("123456789");

        Mockito.when(mockRepository.existsByPhoneNumber(userPhoneNumberToBeUpdate))
                .thenReturn(false);

        userService.addPhoneNumberToAdmin(admin, userPhoneNumberToBeUpdate);

        Mockito.verify(mockRepository, Mockito.times(1)).
                updateUser(admin);
    }

    @Test
    public void addPhoneNumberToAdmin_Should_ThrowException_When_PhoneNumberToAdminIsDuplicate() {
        User admin = createMockUser();
        User userPhoneNumberToBeUpdate = createMockUser();
        userPhoneNumberToBeUpdate.setPhoneNumber("123456789");

        Mockito.when(mockRepository.existsByPhoneNumber(userPhoneNumberToBeUpdate))
                .thenReturn(true);

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userService.addPhoneNumberToAdmin(admin, userPhoneNumberToBeUpdate));
    }

    @Test
    public void deletePhoneNumber_Should_CallRepository_When_PhoneNumberExist() {
        User user = createMockUser();
        User userToDDelete = createMockUser();

        Mockito.when(mockRepository.getById(userToDDelete.getId()))
                .thenReturn(Optional.of(userToDDelete));

        userService.deletePhoneNumber(userToDDelete.getId(), user);

        Mockito.verify(mockRepository, Mockito.times(1))
                .updateUser(user);
    }


}

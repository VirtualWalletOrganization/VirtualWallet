package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.UsersRole;
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

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.utils.UserFilterOptions;
import org.springframework.security.access.AccessDeniedException;

import static com.example.virtualwallet.helpers.createMockUser;
import static com.example.virtualwallet.helpers.createMockUserFilterOptions;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        verify(mockRepository, times(1))
                .getAll(mockUserFilterOptions);
    }

    @Test
    public void getUserById_Should_ReturnUser_When_MatchExists() {
        User user = createMockUser();

        when(mockRepository.getById(user.getId())).thenReturn(Optional.of(user));

        userService.getById(user.getId());

        verify(mockRepository, times(1)).getById(user.getId());
    }

    @Test
    public void getUserByUsername_Should_ReturnUser_When_MatchExists() {
        String username = "testUser";
        User user = new User();

        when(mockRepository.getByUsername(username)).thenReturn(Optional.of(user));

        userService.getByUsername(username);

        verify(mockRepository, times(1)).getByUsername(username);
    }


//    @Test
//    public void registerUser_Should_ThrowException_When_UserWithSameNameExists() {
//        User user = createMockUser();
//        User existingUserWithTheSameName = createMockUser();
//        existingUserWithTheSameName.setId(2);
//
//        Mockito.when(mockRepository.getByUsername(user.getUsername()))
//                .thenReturn(Optional.of(existingUserWithTheSameName));
//
//        Assertions.assertThrows(
//                DuplicateEntityException.class,
//                () -> userService.registerUser(user, new WalletDto()));
//    }


    @Test
    public void update_Should_CallRepository_When_UpdatingExistingUser() {
        User targetUser = createMockUser();
        User executingUser = createMockUser();

        userService.updateUser(targetUser, executingUser);

        verify(mockRepository, times(1))
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

        when(mockRepository.getById(userIdToDelete)).
                thenReturn(Optional.of(userToDelete));

        Assertions.assertThrows(
                EntityAlreadyDeleteException.class,
                () -> userService.deleteUser(userIdToDelete, executingUser));
    }

//    @Test
//    public void updateToAdmin_Should_CallRepository_When_UpdatingExistingUser() {
//        User targetUser = createMockUser();
//        targetUser.getUsersRole().setRole(Role.USER);
//        User executingUser = createMockUser();
//
//        userService.updateToAdmin(targetUser, executingUser);
//
//        Mockito.verify(mockRepository, Mockito.times(1))
//                .updateUser(targetUser);
//    }

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

        verify(mockRepository, times(1))
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

//    @Test
//    public void addPhoneNumberToAdmin_Should_CallRepository_When_PhoneNumberExist() {
//        User admin = createMockUser();
//        User userPhoneNumberToBeUpdate = createMockUser();
//        userPhoneNumberToBeUpdate.setPhoneNumber("123456789");
//
//        Mockito.when(mockRepository.existsByPhoneNumber(userPhoneNumberToBeUpdate))
//                .thenReturn(false);
//
//        userService.addPhoneNumberToAdmin(admin, userPhoneNumberToBeUpdate);
//
//        Mockito.verify(mockRepository, Mockito.times(1)).
//                updateUser(admin);
//    }

    @Test
    public void addPhoneNumberToAdmin_Should_ThrowException_When_PhoneNumberToAdminIsDuplicate() {
        User admin = createMockUser();
        User userPhoneNumberToBeUpdate = createMockUser();
        userPhoneNumberToBeUpdate.setPhoneNumber("123456789");

        when(mockRepository.existsByPhoneNumber(userPhoneNumberToBeUpdate))
                .thenReturn(true);

        Assertions.assertThrows(
                DuplicateEntityException.class,
                () -> userService.addPhoneNumberToAdmin(admin, userPhoneNumberToBeUpdate));
    }

//    @Test
//    public void deletePhoneNumber_Should_CallRepository_When_PhoneNumberExist() {
//        User user = createMockUser();
//        User userToDDelete = createMockUser();
//
//        Mockito.when(mockRepository.getById(userToDDelete.getId()))
//                .thenReturn(Optional.of(userToDDelete));
//
//        userService.deletePhoneNumber(userToDDelete.getId(), user);
//
//        Mockito.verify(mockRepository, Mockito.times(1))
//                .updateUser(user);
//    }

    @Test
    public void testGetProfilePictureUrl_UserExists() {
        // Mocking the behavior of userRepository.getByUsername() to return a user with a profile picture
        String username = "testuser";
        String profilePictureUrl = "http://example.com/profile.jpg";
        User user = new User();
        user.setUsername(username);
        user.setProfilePicture(profilePictureUrl);
        when(mockRepository.getByUsername(username)).thenReturn(Optional.of(user));

        // Calling the method under test
        String result = userService.getProfilePictureUrl(username);

        // Verifying that userRepository.getByUsername() was called once
        verify(mockRepository, times(1)).getByUsername(username);

        // Asserting the expected result
        assertNotNull(result);
        assertEquals(profilePictureUrl, result);
    }

    @Test
    public void testUpdateToUser_SuccessfulUpdate() {
        // Mocking the behavior of targetUser.getUsersRole().getRole() to return a role other than Role.USER
        User targetUser = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.ADMIN);
        targetUser.setUsersRole(usersRole);
        User executingUser = new User();
        UsersRole usersRole2 = new UsersRole();
        usersRole2.setRole(Role.ADMIN);
        executingUser.setUsersRole(usersRole);
        doNothing().when(mockRepository).updateUser(targetUser);

        // Calling the method under test
        userService.updateToUser(targetUser, executingUser);

        // Verifying that userRepository.updateUser() was called once
        verify(mockRepository, times(1)).updateUser(targetUser);

        // Verifying that the user's role was updated to Role.USER
        assertEquals(Role.USER, targetUser.getUsersRole().getRole());
    }

    @Test
    public void testSetAdminRoleIfDataBaseEmpty_DatabaseNotEmpty() {
        // Mocking the behavior of userRepository.isDataBaseEmpty() to return false
        when(mockRepository.isDataBaseEmpty()).thenReturn(false);

        // Creating a user
        User user = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.USER);
        user.setUsersRole(usersRole);

        // Calling the method under test
        userService.setAdminRoleIfDataBaseEmpty(user);

        // Verifying that userRepository.isDataBaseEmpty() was called once
        verify(mockRepository, times(1)).isDataBaseEmpty();

        // Verifying that the user's role was not changed to admin
        assertEquals(Role.USER, user.getUsersRole().getRole());
    }

    @Test
    public void testSetAdminRoleIfDataBaseEmpty_DatabaseEmpty() {
        // Mocking the behavior of userRepository.isDataBaseEmpty() to return true
        when(mockRepository.isDataBaseEmpty()).thenReturn(true);

        // Creating a user
        User user = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.USER);
        user.setUsersRole(usersRole);

        // Calling the method under test
        userService.setAdminRoleIfDataBaseEmpty(user);

        // Verifying that userRepository.isDataBaseEmpty() was called once
        verify(mockRepository, times(1)).isDataBaseEmpty();

        // Verifying that the user's role was changed to admin
        assertEquals(Role.ADMIN, user.getUsersRole().getRole());
    }

    @Test
    public void testIsSameUser_IdenticalUsers() {
        // Creating two identical users
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPhoneNumber("123456789");
        user1.setEmail("test@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setUsername("testuser");
        user2.setPhoneNumber("123456789");
        user2.setEmail("test@example.com");
        user2.setPassword("password123");

        // Calling the method under test
        boolean result = userService.isSameUser(user1, user2);

        // Verifying that the method returns true
        assertTrue(result);
    }

    @Test
    public void testIsSameUser_DifferentUsers() {
        // Creating two different users
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setPhoneNumber("123456789");
        user1.setEmail("test1@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setUsername("testuser2");
        user2.setPhoneNumber("987654321");
        user2.setEmail("test2@example.com");
        user2.setPassword("password456");

        // Calling the method under test
        boolean result = userService.isSameUser(user1, user2);

        // Verifying that the method returns false
        assertFalse(result);
    }

    @Test
    public void testIsSameUser_PartiallyIdenticalUsers() {
        // Creating two partially identical users
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPhoneNumber("123456789");
        user1.setEmail("test1@example.com");
        user1.setPassword("password123");

        User user2 = new User();
        user2.setUsername("testuser");
        user2.setPhoneNumber("987654321");
        user2.setEmail("test2@example.com");
        user2.setPassword("password123");

        // Calling the method under test
        boolean result = userService.isSameUser(user1, user2);

        // Verifying that the method returns false
        assertFalse(result);
    }
}

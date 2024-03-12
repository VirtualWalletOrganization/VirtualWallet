package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.*;
import com.example.virtualwallet.models.Photo;
import com.example.virtualwallet.models.UsersRole;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

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
    @Mock
    WalletRepository walletRepository;
    @InjectMocks
    UserServiceImpl userService;

    public static final BigDecimal REFERRAL_BONUS = BigDecimal.valueOf(20);

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


    @Test
    public void testGetProfilePictureUrl_UserExists() {

        String username = "testuser";
        String profilePictureUrl = "http://example.com/profile.jpg";
        User user = new User();
        user.setUsername(username);
        user.setProfilePicture(profilePictureUrl);
        when(mockRepository.getByUsername(username)).thenReturn(Optional.of(user));

        String result = userService.getProfilePictureUrl(username);

        verify(mockRepository, times(1)).getByUsername(username);
        assertNotNull(result);
        assertEquals(profilePictureUrl, result);
    }

    @Test
    public void testUpdateToUser_SuccessfulUpdate() {

        User targetUser = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.ADMIN);
        targetUser.setUsersRole(usersRole);
        User executingUser = new User();
        UsersRole usersRole2 = new UsersRole();
        usersRole2.setRole(Role.ADMIN);
        executingUser.setUsersRole(usersRole);
        doNothing().when(mockRepository).updateUser(targetUser);

        userService.updateToUser(targetUser, executingUser);

        verify(mockRepository, times(1)).updateUser(targetUser);

        assertEquals(Role.USER, targetUser.getUsersRole().getRole());
    }

    @Test
    public void testSetAdminRoleIfDataBaseEmpty_DatabaseNotEmpty() {

        when(mockRepository.isDataBaseEmpty()).thenReturn(false);

        User user = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.USER);
        user.setUsersRole(usersRole);

        userService.setAdminRoleIfDataBaseEmpty(user);

        verify(mockRepository, times(1)).isDataBaseEmpty();

        assertEquals(Role.USER, user.getUsersRole().getRole());
    }

    @Test
    public void testSetAdminRoleIfDataBaseEmpty_DatabaseEmpty() {

        when(mockRepository.isDataBaseEmpty()).thenReturn(true);

        User user = new User();
        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.USER);
        user.setUsersRole(usersRole);

        userService.setAdminRoleIfDataBaseEmpty(user);

        verify(mockRepository, times(1)).isDataBaseEmpty();

        assertEquals(Role.ADMIN, user.getUsersRole().getRole());
    }

    @Test
    public void testIsSameUser_IdenticalUsers() {

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


        boolean result = userService.isSameUser(user1, user2);

        assertTrue(result);
    }

    @Test
    public void testIsSameUser_DifferentUsers() {

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

        boolean result = userService.isSameUser(user1, user2);

        assertFalse(result);
    }

    @Test
    public void testIsSameUser_PartiallyIdenticalUsers() {

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

        boolean result = userService.isSameUser(user1, user2);

        assertFalse(result);
    }

    @Test
    public void testCheckDuplicateEntity_NonExistingEntities() {

        when(mockRepository.getByUsername(anyString())).thenReturn(Optional.empty());
        when(mockRepository.getByEmail(anyString())).thenReturn(Optional.empty());

        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");

        try {
            userService.checkDuplicateEntity(user);
        } catch (DuplicateEntityException e) {
            fail("Unexpected DuplicateEntityException thrown");
        }
    }

    @Test
    public void testAddBonus_UserWithDefaultWallet() {

        User user = new User();
        Wallet defaultWallet = new Wallet();
        defaultWallet.setDefault(true);
        defaultWallet.setBalance(BigDecimal.valueOf(100)); // Initial balance
        Set<Wallet> wallets = new HashSet<>();
        wallets.add(defaultWallet);
        user.setWallets(wallets);

        doNothing().when(walletRepository).update(defaultWallet);

        userService.addBonus(user);

        verify(walletRepository, times(1)).update(defaultWallet);

        BigDecimal expectedBalance = BigDecimal.valueOf(100).add(REFERRAL_BONUS);
        assertEquals(expectedBalance, defaultWallet.getBalance());
    }
}

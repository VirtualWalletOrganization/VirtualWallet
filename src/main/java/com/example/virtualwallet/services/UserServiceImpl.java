package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DeletionRestrictedException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityAlreadyDeleteException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.Photo;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.UsersRole;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Identity;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.repositories.contracts.ReferralRepository;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.utils.UserFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.*;
import static com.example.virtualwallet.utils.Messages.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final WalletRepository walletRepository;
    private final SessionFactory sessionFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ReferralRepository referralRepository, WalletRepository walletRepository, SessionFactory sessionFactory) {
        this.userRepository = userRepository;
        this.referralRepository = referralRepository;
        this.walletRepository = walletRepository;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAll(User user, UserFilterOptions userFilterOptions) {
        return this.userRepository.getAll(userFilterOptions);
    }

    @Override
    public long getAllNumber() {
        return this.userRepository.getAllNumber();
    }

    @Override
    public User getById(int id) {
        return userRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(id)));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }


    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    public User getByPhoneNumber(String phoneNumber) {
        return userRepository.getByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("User", "phone number", phoneNumber));
    }

    @Override
    public List<User> getAllUsersByWalletId(int walletId) {
        return userRepository.getAllUsersByWalletId(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Users", "wallet id", String.valueOf(walletId)));
    }

    @Override
    public void registerUser(User user) {
        setAdminRoleIfDataBaseEmpty(user);
        User existingUser = getByUsernameWhenRegistering(user.getUsername());

        if (existingUser != null && isSameUser(existingUser, user) && existingUser.isDeleted()) {
            existingUser.setDeleted(false);
            userRepository.updateUser(existingUser);
        } else {
            checkDuplicateEntity(user);

            userRepository.registerUser(user);
        }
    }

    @Override
    public void createPhoto(Photo photo, User user) {
        User user1 = getByUsername(user.getUsername());
        photo.setCreator(user1);
        userRepository.createPhoto(photo, user);
        userRepository.updateUser(user);
    }

    @Override
    public User getByUsernameWhenRegistering(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            List<User> result = query.list();

            return result.isEmpty() ? null : result.get(0);
        }
    }

    @Override
    public void confirmUserRegistration(User currentUser, User user) {
        checkAccessPermissionsAdmin(currentUser, VERIFY_USER);
        User userToBeVerified = getById(user.getId());

        if (userToBeVerified == null) {
            throw new EntityNotFoundException("User", "email", user.getEmail());
        }

        if (user.getIdentityStatus().getIdentity().equals(Identity.APPROVED)) {
            throw new DuplicateEntityException("User", "id", String.valueOf(user.getId()), ALREADY_APPROVED);
        } else if (userToBeVerified.getPhoto().getSelfie() == null || userToBeVerified.getPhoto().getCardId() == null) {
            userToBeVerified.getIdentityStatus().setIdentity(Identity.REJECTED);
        } else {
            user.getIdentityStatus().setIdentity(Identity.APPROVED);
            user.setStatus(UserStatus.ACTIVE);
            userRepository.confirmRegistration(user);

//            if (referralRepository.getReferralEmail(user.getEmail()).isPresent()) {
//                if (referralRepository.getReferralStatusByEmail(user.getEmail()).equals(Status.PENDING)) {
//                    User referrerUser = referralRepository.getReferrerUserIdByEmail(user.getEmail()).get();
//                    addBonus(referrerUser);
//                    addBonus(user);
//                }
//            }
        }
    }

    @Override
    public void updateUser(User executingUser, User targetUser) {
        checkAccessPermissionsUser(targetUser.getId(), executingUser, MODIFY_USER_MESSAGE_ERROR);

        if (!targetUser.getUsername().equals(executingUser.getUsername())) {
            throw new EntityNotFoundException("User", "username", targetUser.getUsername());
        }

        if (!targetUser.getEmail().equals(executingUser.getEmail())) {
            if (userRepository.getByEmail(targetUser.getEmail()).isPresent()) {
                throw new DuplicateEntityException("User", "email", targetUser.getEmail());
            }
        }

        userRepository.updateUser(targetUser);
    }

    @Override
    public void deleteUser(int deleteUser, User executingUser) {
        checkAccessPermissions(deleteUser, executingUser, DELETE_USER_MESSAGE_ERROR);
        User userToDelete = getById(deleteUser);

        if (userToDelete.isDeleted()) {
            throw new EntityAlreadyDeleteException("User", "id", String.valueOf(deleteUser));
        }

        if (userToDelete.getId() == 1) {
            throw new DeletionRestrictedException(MASTER_ADMIN_MESSAGE_ERROR);
        }

        userToDelete.setDeleted(true);
        userRepository.updateUser(userToDelete);
    }

    @Override
    public void saveProfilePictureUrl(String username, String profilePictureUrl) {
        Optional<User> userOptional = userRepository.getByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getPhoto().setSelfie(profilePictureUrl);
            userRepository.updateUser(user);
        } else {
            throw new EntityNotFoundException("User", "username", username);
        }
    }

    @Override
    public String getProfilePictureUrl(String username) {
        Optional<User> userOptional = userRepository.getByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getProfilePicture();
        } else {
            throw new EntityNotFoundException("User", "username", username);
        }
    }

    @Override
    public void updateToAdmin(User targetUser, User executingUser) {
        if (targetUser.getUsersRole().getRole() == Role.ADMIN) {
            throw new DuplicateEntityException(
                    "User", "id", String.valueOf(targetUser.getId()), " is already an admin.");
        }

        checkAccessPermissionsAdmin(executingUser, UPDATE_TO_ADMIN_ERROR_MESSAGE);
//        UsersRole usersRole = new UsersRole();
//        usersRole.setRole(Role.ADMIN);
//        targetUser.setUsersRole(usersRole);
        userRepository.updateUserToAdmin(targetUser, executingUser);
    }


    @Override
    public void updateToUser(User targetUser, User executingUser) {
        if (targetUser.getUsersRole().getRole() == Role.USER) {
            throw new DuplicateEntityException(
                    "User", "id", String.valueOf(targetUser.getId()), " is already an user.");
        }

        checkAccessPermissionsAdmin(executingUser, UPDATE_TO_USER_ERROR_MESSAGE);

        if (targetUser.getId() == 1) {
            throw new DeletionRestrictedException(MASTER_ADMIN_MESSAGE_ERROR);
        }

        UsersRole usersRole = new UsersRole();
        usersRole.setRole(Role.USER);
        usersRole.setId(usersRole.getRole().ordinal() + 1);
        targetUser.setUsersRole(usersRole);
        userRepository.updateUser(targetUser);
    }

    @Override
    public void blockUser(User admin, User blockUser) {
        checkAccessPermissionsAdmin(admin, MODIFY_ADMIN_MESSAGE_ERROR);

        if (blockUser.getStatus() == UserStatus.BLOCKED) {
            throw new DuplicateEntityException(
                    "User", "id", String.valueOf(blockUser.getId()), "has already been blocked");
        }

        if (blockUser.getId() == 1) {
            throw new DeletionRestrictedException(MASTER_ADMIN_MESSAGE_ERROR_BLOCK);
        }

        blockUser.setStatus(UserStatus.BLOCKED);
        userRepository.updateUser(blockUser);
    }

    @Override
    public void unBlockUser(User admin, User unBlockUser) {
        checkAccessPermissionsAdmin(admin, MODIFY_ADMIN_MESSAGE_ERROR);

        if (unBlockUser.getStatus() == UserStatus.ACTIVE) {
            throw new DuplicateEntityException(
                    "User", "id", String.valueOf(unBlockUser.getId()), "has already been activated");
        }

        unBlockUser.setStatus(UserStatus.ACTIVE);
        userRepository.updateUser(unBlockUser);
    }

    @Override
    public void addPhoneNumberToAdmin(User admin, User userPhoneNumberToBeUpdate) {
        checkAccessPermissionsAdmin(admin, UPDATE_PHONE_NUMBER_ERROR_MESSAGE);
        checkAccessPermissionsUser(admin.getId(), userPhoneNumberToBeUpdate, UPDATE_PHONE_NUMBER_ERROR_MESSAGE);

        if (admin.getPhoneNumber() != null
                && userPhoneNumberToBeUpdate.getPhoneNumber().isEmpty()) {
            updateUser(admin, userPhoneNumberToBeUpdate);

        } else if (userPhoneNumberToBeUpdate.getPhoneNumber() != null
                && !userPhoneNumberToBeUpdate.getPhoneNumber().isEmpty()) {

            if (userRepository.existsByPhoneNumber(userPhoneNumberToBeUpdate)
                    && !admin.getPhoneNumber().equals(userPhoneNumberToBeUpdate.getPhoneNumber())) {
                throw new DuplicateEntityException("Admin", "phone number", userPhoneNumberToBeUpdate.getPhoneNumber());
            }

            updateUser(admin, userPhoneNumberToBeUpdate);

        } else {
            throw new EntityNotFoundException("Admin", "phone number");
        }
    }

    @Override
    public void deletePhoneNumber(int userId, User user) {
        User userToDelete = getById(userId);
        checkAccessPermissionsAdmin(userToDelete, DELETE_PHONE_NUMBER_MESSAGE_ERROR);
        checkAccessPermissionsUser(userId, user, DELETE_PHONE_NUMBER_MESSAGE_ERROR);

        if (userToDelete.isDeleted()) {
            throw new EntityAlreadyDeleteException("User", "id", String.valueOf(userId));
        }

        if (userToDelete.getPhoneNumber() == null) {
            throw new EntityAlreadyDeleteException(
                    "User's phone number", "id", String.valueOf(userId));
        }

        userToDelete.setPhoneNumber(null);
        userRepository.updateUser(userToDelete);
    }

    public void addBonus(User user) {
        Wallet userWallet = user.getWallets().stream()
                .filter(Wallet::getDefault)
                .findFirst().get();
        userWallet.setBalance(userWallet.getBalance().add(REFERRAL_BONUS));
        walletRepository.update(userWallet);
    }

    public void checkDuplicateEntity(User user) {
        if (userRepository.getByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        if (userRepository.getByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
        if (userRepository.getByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new DuplicateEntityException("User", "phone number", user.getPhoneNumber());
        }
    }

    public static boolean isSameUser(User existingUser, User user) {
        return existingUser.getUsername().equals(user.getUsername())
                && existingUser.getPhoneNumber().equals(user.getPhoneNumber())
                && existingUser.getEmail().equals(user.getEmail())
                && existingUser.getPassword().equals(user.getPassword());
    }

    public void setAdminRoleIfDataBaseEmpty(User user) {
        if (userRepository.isDataBaseEmpty()) {
            user.getUsersRole().setRole(Role.ADMIN);
        }
    }
}
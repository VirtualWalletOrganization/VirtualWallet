package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.DeletionRestrictedException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityAlreadyDeleteException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.repositories.contracts.UserRepository;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.utils.UserFilterOptions;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.virtualwallet.utils.CheckPermission.*;
import static com.example.virtualwallet.utils.Messages.*;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return this.userRepository.getById(id);
    }

    @Override
    public User getByUsername(String username) {
        return this.userRepository.getByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return this.userRepository.getByEmail(email);
    }



    @Override
    public void registerUser(User user) {
        setAdminRoleIfDataBaseEmpty(user);
        User existingUser = userRepository.getByUsername(user.getUsername());

        if (existingUser != null && isSameUser(existingUser, user) && existingUser.isDeleted()) {
            userRepository.reactivated(existingUser);
        } else {
            checkDuplicateEntity(user);
            userRepository.registerUser(user);
        }
    }

    @Override
    public void updateUser(User executingUser, User targetUser) {
        checkAccessPermissionsUser(targetUser.getId(), executingUser, MODIFY_USER_MESSAGE_ERROR);

        if (!targetUser.getUsername().equals(executingUser.getUsername())) {
            throw new com.example.virtualwallet.exceptions.EntityNotFoundException("User", "username", targetUser.getUsername());
//
        }

        if (!targetUser.getEmail().equals(executingUser.getEmail())) {
            if (userRepository.getByEmail(targetUser.getEmail()) != null) {
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

        userRepository.deleteUser(deleteUser);
    }

    @Override
    public void saveProfilePictureUrl(String username, String profilePictureUrl) {
        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new com.example.virtualwallet.exceptions.EntityNotFoundException("User", "username", username);
        }

        user.getPhoto().setSelfie(profilePictureUrl);
        userRepository.updateUser(user);
    }

    @Override
    public String getProfilePictureUrl(String username) {
        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new EntityNotFoundException("User", "username", username);
        }

        return user.getPhoto().getSelfie();
    }

    @Override
    public void updateToAdmin(User targetUser, User executingUser) {
        if (targetUser.getRole() == Role.ADMIN) {
            throw new DuplicateEntityException(
                    "User", "id", String.valueOf(targetUser.getId()), " is already an admin.");
        }

        checkAccessPermissionsAdmin(executingUser, UPDATE_TO_ADMIN_ERROR_MESSAGE);
        targetUser.setRole(Role.ADMIN);
        userRepository.updateUser(targetUser);
    }

    @Override
    public void updateToUser(User targetUser, User executingUser) {
        if (targetUser.getRole() == Role.USER) {
            throw new DuplicateEntityException(
                    "User", "id", String.valueOf(targetUser.getId()), " is already an user.");
        }

        checkAccessPermissionsAdmin(executingUser, UPDATE_TO_USER_ERROR_MESSAGE);

        if (targetUser.getId() == 1) {
            throw new DeletionRestrictedException(MASTER_ADMIN_MESSAGE_ERROR);
        }

        targetUser.setRole(Role.USER);
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
            throw new com.example.virtualwallet.exceptions.EntityNotFoundException("Admin", "phone number");
        }
    }

    @Override
    public void deletePhoneNumber(int userId, User user) {
        User userToDelete = userRepository.getById(userId);
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

    private void checkDuplicateEntity(User user) {
        if (userRepository.getByUsername(user.getUsername()) != null) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        if (userRepository.getByEmail(user.getEmail()) != null) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }
    }

    private static boolean isSameUser(User existingUser, User user) {
        return existingUser.getUsername().equals(user.getUsername())
                && existingUser.getPhoneNumber().equals(user.getPhoneNumber())
                && existingUser.getEmail().equals(user.getEmail())
                && existingUser.getPassword().equals(user.getPassword());
    }
    private void setAdminRoleIfDataBaseEmpty(User user) {
        if (userRepository.isDataBaseEmpty()) {
            user.setRole(Role.ADMIN);
        }
    }
}

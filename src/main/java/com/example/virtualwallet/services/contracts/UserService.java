package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.utils.UserFilterOptions;

import java.util.List;

public interface UserService {

    List<User> getAll(User user, UserFilterOptions userFilterOptions);

    long getAllNumber();

    User getById(int id);

    User getByUsername(String username);

    User getByUsername2(String username);

    User getByEmail(String email);

    User getByPhoneNumber(String phoneNumber);

    void registerUser(User user);

    void updateUser(User executingUser, User targetUser);

    void deleteUser(int deleteUserId, User executingUser);

    User confirmUserRegistration(User currentUser, User user);

    void saveProfilePictureUrl(String username, String profilePictureUrl);

    String getProfilePictureUrl(String username);

    void updateToAdmin(User updateToAdmin, User userAdmin);

    void updateToUser(User targetUser, User executingUser);

    void blockUser(User admin, User blockUser);

    void unBlockUser(User admin, User unBlockUser);

    void addPhoneNumberToAdmin(User admin, User userPhoneNumberToBeUpdate);

    void deletePhoneNumber(int userId, User user);
}

package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.Photo;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.utils.UserFilterOptions;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getAll(UserFilterOptions userFilterOptions);

    long getAllNumber();

    Optional<User> getById(int id);

    Optional<User> getByUsername(String username);

    Optional<User> getByEmail(String email);

    Optional<User> getByPhoneNumber(String phoneNumber);

    void registerUser(User user);

    void confirmRegistration(User user);

    Photo createPhoto(Photo photo, User user);

    void reactivated(User targetUser);
    Optional<List<User>> getAllUsersByWalletId(int walletId);

    void updateUser(User targetUser);

    void deleteUser(User targetUser);

    boolean isDataBaseEmpty();

    void updateUserToAdmin(User targetUser, User executingUser);

    boolean existsByPhoneNumber(User userPhoneNumberToBeUpdate);
}
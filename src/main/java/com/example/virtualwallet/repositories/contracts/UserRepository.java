package com.example.virtualwallet.repositories.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.utils.UserFilterOptions;

import java.util.List;

public interface UserRepository {

    List<User> getAll(UserFilterOptions userFilterOptions);

    long getAllNumber();

    User getById(int id);

    User getByUsername(String username);

    User getByEmail(String email);

    void registerUser(User user);

    void reactivated(User targetUser);

    void updateUser(User targetUser);

    void deleteUser(int targetUserId);

    boolean isDataBaseEmpty();

    boolean existsByPhoneNumber(User userPhoneNumberToBeUpdate);
}
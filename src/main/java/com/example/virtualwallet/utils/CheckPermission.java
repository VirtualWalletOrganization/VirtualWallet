package com.example.virtualwallet.utils;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Role;

public class CheckPermission {
    public static void checkAccessPermissions(int targetUserId, User executingUser, String message) {
        if (!executingUser.getRole().name().equals("ADMIN") && executingUser.getId() != targetUserId) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkAccessPermissionsAdmin(User executingUser, String message) {
        if (!executingUser.getRole().equals(Role.ADMIN)) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkAccessPermissionsUser(int targetUserId, User executingUser, String message) {
        if (executingUser.getId() != targetUserId) {
            throw new AuthorizationException(message);
        }
    }
}

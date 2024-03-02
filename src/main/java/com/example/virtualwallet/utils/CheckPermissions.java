package com.example.virtualwallet.utils;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.models.enums.WalletRole;

public class CheckPermissions {

    public static void checkAccessPermissions(int targetUserId, User executingUser, String message) {
        if (!executingUser.getUsersRole().getRole().name().equals("ADMIN") && executingUser.getId() != targetUserId) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkAccessPermissionsAdmin(User executingUser, String message) {
        if (!executingUser.getUsersRole().getRole().equals(Role.ADMIN)) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkAccessPermissionsUser(int targetUserId, User executingUser, String message) {
        if (executingUser.getId() != targetUserId) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkBlockOrDeleteUser(User user, String message) {
        if (user.getStatus() == UserStatus.BLOCKED || user.isDeleted()) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkUserWalletAdmin(Wallet wallet, User user, String message) {
        if (!wallet.getCreator().equals(user) || !user.getWalletsRole().equals(WalletRole.ADMIN)) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkAccessPermissionWalletUser(Wallet wallet, User user, String message) {
        if (wallet.getCreator().getId() != user.getId()) {
            throw new AuthorizationException(message);
        }
    }
}
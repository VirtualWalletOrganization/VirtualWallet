package com.example.virtualwallet.utils;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.models.enums.WalletRole;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (!wallet.getCreator().equals(user) || !user.getWalletsRole().getWalletRole().equals(WalletRole.ADMIN)) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkAccessPermissionWalletUser(Wallet wallet, User user, String message) {
        if (wallet.getCreator().getId() != user.getId()) {
            throw new AuthorizationException(message);
        }
    }

    public static void checkPermissionExistingUsersInWallet(Wallet wallet, User user, String message) {
        wallet.getUsers().stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst().orElseThrow(() -> new AuthorizationException(message));
    }

    public static void checkPermissionShowingCardsByUser(List<Card> cards, User user, String message) {
        Set<String> userCardNumbers = user.getCards().stream()
                .map(Card::getCardNumber)
                .collect(Collectors.toSet());

        for (Card card : cards) {
            if (!userCardNumbers.contains(card.getCardNumber())) {
                throw new AuthorizationException(message);
            }
        }
    }

    public static void checkPermissionShowingWalletsByUser(List<Wallet> wallets, User user, String message) {
        Set<Integer> userWallets = user.getWallets().stream()
                .map(Wallet::getId)
                .collect(Collectors.toSet());

        for (Wallet wallet : wallets) {
            if (!userWallets.contains(wallet.getId())) {
                throw new AuthorizationException(message);
            }
        }
    }
}
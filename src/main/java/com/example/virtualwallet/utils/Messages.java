package com.example.virtualwallet.utils;

import java.math.BigDecimal;
import java.time.Duration;

public class Messages {

    public static final String UNAUTHORIZED_USER_ERROR_MESSAGE = "You are not authorized to browse user information.";
    public static final String MASTER_ADMIN_MESSAGE_ERROR = "Master admin user can not be downgrade to user.";
    public static final String MASTER_ADMIN_MESSAGE_ERROR_BLOCK = "Master admin user can not be blocked.";
    public static final String DELETE_USER_MESSAGE_ERROR = "Only owner of this account can delete this profile.";
    public static final String MODIFY_USER_MESSAGE_ERROR = "Only user creator is able to update their profile information.";
    public static final String MODIFY_ADMIN_MESSAGE_ERROR = "Only admin is able to block or unblock individual users";
    public static final String UPDATE_PHONE_NUMBER_ERROR_MESSAGE = "Only owner admin can update phone number.";
    public static final String UPDATE_TO_ADMIN_ERROR_MESSAGE = "Only admin can make other user an admin";
    public static final String UPDATE_TO_USER_ERROR_MESSAGE = "Only admin can adjust the role of the user.";
    public static final String DELETE_PHONE_NUMBER_MESSAGE_ERROR = "Only owner admin can delete phone number.";
    public static final String USER_HAS_BEEN_BLOCKED_OR_DELETED = "User has been blocked or deleted.";
    public static final String UPDATE_USER_MESSAGE_ERROR = "Only user creator is able to update.";
    public static final String MODIFY_CARD_ERROR_MESSAGE = "“Only the owner of this card has permission to delete or update it.";
    public static final String CARD_ERROR_MESSAGE = "“Only admin can review all cards.";
    public static final String ADD_CARD_ERROR_MESSAGE = "Only the owner of this wallet has permission to add a card.";
    public static final String SEARCH_CARD_ERROR_MESSAGE = "Only the owner of this card has permission to review it.";
    public static final String CARD_IS_EXPIRED_OR_DEACTIVATED = "Card is expired or deactivated";
    public static final String SEARCH_WALLET_ERROR_MESSAGE = "You are not authorize to review, update or delete this wallet.";
    public static final String CARD_MISMATCH_ERROR = "Card holder name does not match user name";
    public static final String INVALID_CARD = "Card number must be 16 digits and check number must be 3 digits.";
    public static final String ADD_USER_TO_WALLET = "You are not authorized to add users to this wallet.";
    public static final String REMOVE_USER_FROM_WALLET = "You are not authorized to remove users from this wallet.";
    public static final String ALREADY_APPROVED = "has already been approved.";
    public static final String VERIFY_USER = "Only admin can verify users.";
    public static final BigDecimal REFERRAL_BONUS = BigDecimal.valueOf(20);
    public final static String USER_NOT_FOUND_MSG = "user with username %s not found";
    public static final String WALLET_ERROR_MESSAGE = "Only admin can review all wallets";
    public static final String ERROR_USER_FOUND = "Only users with access could review this wallet.";
    public static final String MODIFY_WALLET_ERROR_MESSAGE = "“Only the owner of this wallet has permission to delete or update it.";
    public static final String ERROR_INSUFFICIENT_BALANCE = "Insufficient balance.";
    public static final String SUCCESS_TRANSFER = "Transfer successful";
    public static final String INVALID_REQUEST = "Invalid request data";
    public static final String ERROR_TRANSACTION = "You are not authorized to complete this transaction.";


}
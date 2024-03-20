package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.helpers.CardMapper;
import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.CardDto;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.WalletRole;
import com.example.virtualwallet.models.enums.WalletType;
import com.example.virtualwallet.repositories.contracts.WalletRepository;
import com.example.virtualwallet.services.contracts.TransferService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.example.virtualwallet.utils.CheckPermissions.*;
import static com.example.virtualwallet.utils.Messages.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;
    private final TransferService transferService;
    private final CardMapper cardMapper;
    private final WebClient dummyApi;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, UserService userService,
                             TransferService transferService,
                             CardMapper cardMapper, WebClient dummyApi) {
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.transferService = transferService;
        this.cardMapper = cardMapper;
        this.dummyApi = dummyApi;
    }

    @Override
    public List<Wallet> getAll(User user) {
        checkAccessPermissionsAdmin(user, WALLET_ERROR_MESSAGE);
        return walletRepository.getAll();
    }

    @Override
    public List<Wallet> getAllWalletsByUserId(User user) {
        List<Wallet> wallets = walletRepository.getAllWalletsByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cards"));
        checkPermissionShowingWalletsByUser(wallets, user, SEARCH_WALLET_ERROR_MESSAGE);
        return wallets;
    }

    @Override
    public Wallet getWalletById(int walletId, int userId) {
        User user = userService.getById(userId);
        Wallet wallet = walletRepository.getWalletById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));
        checkPermissionExistingUsersInWallet(wallet, user, SEARCH_WALLET_ERROR_MESSAGE);

        return wallet;
    }

    @Override
    public List<Wallet> getWalletsByCardId(int cardId, int userId) {
        userService.getById(userId);
        return walletRepository.getWalletsByCardId(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet", " card id", String.valueOf(cardId)));
    }

    @Override
    public Wallet getDefaultWallet(int recipientUserId) {
        return walletRepository.getDefaultWallet(recipientUserId)
                .orElseThrow(() -> new EntityNotFoundException("Default's wallet"));
    }

    @Override
    public List<Wallet> getAllWalletsByCreatorId(int creatorId) {
        return walletRepository.getAllWalletsByCreatorId(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("Wallets"));
    }


    @Override
    public Wallet create(Wallet wallet, User user) {
        if (wallet.getWalletsType().getWalletType().equals(WalletType.JOINT)) {
            User userCreator = wallet.getCreator();
            WalletsRole walletsRole = new WalletsRole();
            walletsRole.setId(WalletRole.ADMIN.ordinal() + 1);
            walletsRole.setWalletRole(WalletRole.ADMIN);
            userCreator.setWalletsRole(walletsRole);
            wallet.setCreator(userCreator);
        }

        if (wallet.getDefault()) {
            checkDefaultWallets(wallet, user);
        }

        Wallet walletToAdd = walletRepository.create(wallet);
        user.getWallets().add(walletToAdd);
        userService.updateUser(user, user);
        return walletToAdd;
    }

    @Override
    public void update(Wallet walletToUpdate, User user) {
        checkPermissionExistingUsersInWallet(walletToUpdate, user, MODIFY_WALLET_ERROR_MESSAGE);
        checkDefaultWallets(walletToUpdate, user);
        walletRepository.update(walletToUpdate);

    }

    @Override
    public void updateRecurringTransaction(Wallet wallet) {
        walletRepository.update(wallet);
    }

    @Override
    public void delete(Wallet walletToDelete, User currentUser) {
        if (walletToDelete.getWalletsType().getWalletType() == WalletType.JOINT) {
            checkUserWalletAdmin(walletToDelete, currentUser, DELETE_WALLET);
        }

        walletToDelete.setDeleted(true);
        List<User> users = userService.getAllUsersByWalletId(walletToDelete.getId());

        for (User user : users) {
            user.getWallets().forEach(wallet -> {
                if (wallet.getId() == walletToDelete.getId()) {
                    wallet.setDeleted(true);
                    user.getWallets().remove(wallet);
                    wallet.getUsers().remove(user);
                    walletRepository.update(wallet);
                }
            });

            walletRepository.update(walletToDelete);
        }
    }

    @Override
    public void addUsersToWallet(int walletId, int userToAddId, User executingUser) {
        Wallet wallet = getWalletById(walletId, executingUser.getId());
        User userToAdd = userService.getById(userToAddId);

        if (!wallet.getWalletsType().getWalletType().equals(WalletType.JOINT)) {
            throw new EntityNotFoundException("Joint wallet", "id", String.valueOf(walletId));
        }

        checkUserWalletAdmin(wallet, executingUser, ADD_USER_TO_WALLET);

        if (wallet.getUsers().contains(userToAdd)) {
            throw new DuplicateEntityException("User", "id", "one of the provided user IDs");
        }

        userToAdd.getWallets().stream()
                .filter(w -> wallet.getId() == walletId && wallet.getDeleted())
                .forEach(w -> wallet.setDeleted(false));


        wallet.getUsers().add(userToAdd);
        walletRepository.update(wallet);
        userToAdd.getWallets().add(wallet);
        userService.updateUser(userToAdd, userToAdd);
    }

    @Override
    public void removeUsersFromWallet(int walletId, int userId, User executingUser) {
        Wallet wallet = getWalletById(walletId, executingUser.getId());
        User userToRemove = userService.getById(userId);

        if (executingUser.getId() == userId) {
            throw new AuthorizationException(REMOVE_YOURSELF_FROM_WALLET);
        }

        checkUserWalletAdmin(wallet, executingUser, REMOVE_USER_FROM_WALLET);

        wallet.getUsers().remove(userToRemove);
        walletRepository.update(wallet);
        userToRemove.getWallets().remove(wallet);
        userService.updateUser(userToRemove, userToRemove);
    }

    private void checkDefaultWallets(Wallet wallet, User user) {
        Optional<Wallet> currentDefaultWallet = walletRepository.getDefaultWallet(user.getId());

        if (currentDefaultWallet.isPresent()) {
            if (currentDefaultWallet.get().getId() != wallet.getId()) {
                currentDefaultWallet.get().setDefault(false);
                walletRepository.update(currentDefaultWallet.get());
                wallet.setDefault(true);
            }
        } else {
            wallet.setDefault(true);
        }
    }

    @Override
    public String moneyFromCardToWallet(Transfer transfer, Wallet receiverWallet, User user, Card card) {
        Transfer newTransfer = transferService.createTransfer(transfer);

        String response = sendTransferRequest(card);

        if (response.equals("COMPLETED")) {
            receiverWallet.setBalance(receiverWallet.getBalance().add(newTransfer.getAmount()));
            walletRepository.update(receiverWallet);
            user.getWallets().stream()
                    .filter(wallet -> wallet.getId() == receiverWallet.getId())
                    .forEach(wallet -> {
                        wallet.setBalance(receiverWallet.getBalance());
                    });
            userService.updateUser(user, user);
            newTransfer.setStatus(Status.COMPLETED);
            transferService.updateTransfer(newTransfer);
        } else if (response.equals("REJECTED")) {
            response = "REJECTED";
        }

        return response;
    }

    private String sendTransferRequest(Card card) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = dummyApi.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(DUMMY_API_COMPLETE_URL));
        CardDto cardDto = cardMapper.toDtoCard(card);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(cardDto);
        Mono<String> response = headersSpec.retrieve().bodyToMono(String.class);
        return response.block();
    }
}
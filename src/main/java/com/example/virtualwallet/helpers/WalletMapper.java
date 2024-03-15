package com.example.virtualwallet.helpers;

import com.example.virtualwallet.exceptions.DuplicateEntityException;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.WalletsType;
import com.example.virtualwallet.models.dtos.RegisterDto;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.models.enums.WalletType;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WalletMapper {

    private final WalletService walletService;

    public WalletMapper(WalletService walletService) {
        this.walletService = walletService;
    }



    public Wallet fromDto(WalletDto dto, User user) {
        Wallet wallet = new Wallet();
        wallet.setCurrency(dto.getCurrency());

        WalletsType walletsType = new WalletsType();
        walletsType.setId(dto.getWalletType().ordinal());
        walletsType.setWalletType(WalletType.valueOf(dto.getWalletType().name()));
        walletsType.setId(dto.getWalletType().ordinal() + 1);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setWalletsType(walletsType);
        wallet.setCreator(user);
        return wallet;
    }

    public WalletDto toDto(Wallet wallet) {
        WalletDto walletDto = new WalletDto();
        walletDto.setCurrency(wallet.getCurrency());
        walletDto.setWalletType(WalletType.valueOf(wallet.getWalletsType().getWalletType().name()));
        walletDto.setDefault(wallet.getDefault());

        return walletDto;
    }

    public Wallet fromDtoCreateWallet(RegisterDto registerDto,User user) {
        Wallet wallet = new Wallet();
        wallet.setCreator(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(registerDto.getCurrency());
        WalletsType walletsType=new WalletsType();
        walletsType.setId(WalletType.REGULAR.ordinal()+1);
        walletsType.setWalletType(WalletType.REGULAR);
        wallet.setWalletsType(walletsType);
        wallet.setDefault(true);


        return wallet;
    }
}
package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Card;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.WalletsType;
import com.example.virtualwallet.models.dtos.CardDto;
import com.example.virtualwallet.models.dtos.WalletDto;
import com.example.virtualwallet.models.enums.CardType;
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

    public Wallet fromDto(int id, WalletDto dto,int userId) {
        Wallet wallet = walletService.getWalletById(id,userId);
        wallet.setDefault(dto.getDefault());
        return wallet;
    }
    public Wallet fromDto(WalletDto dto) {
        Wallet wallet = new Wallet();
        wallet.setCurrency(dto.getCurrency());

        WalletsType walletsType = new WalletsType();
        walletsType.setId(dto.getWalletType().ordinal());
        walletsType.setWalletType(WalletType.valueOf(dto.getWalletType().name()));
        walletsType.setId(dto.getWalletType().ordinal()+1);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setWalletsType(walletsType);
        return wallet;
    }
}
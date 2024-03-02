package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.WalletsType;
import com.example.virtualwallet.models.dtos.WalletDto;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public Wallet fromDtoRegister(WalletDto dto) {
        Wallet wallet = new Wallet();
        wallet.setCurrency(dto.getCurrency());

        WalletsType walletsType = new WalletsType();
        walletsType.setId(dto.getWalletType().ordinal());

        String name = dto.getWalletType().getName();
        name = walletsType.getWalletType().getName();

//        walletsType.getWalletType()(dto.getWalletType().name());

        wallet.setWalletsType(walletsType);
        wallet.setDefault(true);
        return wallet;
    }
}
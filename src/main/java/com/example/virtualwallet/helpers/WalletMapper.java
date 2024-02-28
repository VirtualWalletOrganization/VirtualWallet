package com.example.virtualwallet.helpers;

import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.WalletDto;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public Wallet fromDtoRegister(WalletDto dto) {
        Wallet wallet = new Wallet();
        wallet.setCurrency(dto.getCurrency());
        wallet.setDefault(true);
        return wallet;
    }
}
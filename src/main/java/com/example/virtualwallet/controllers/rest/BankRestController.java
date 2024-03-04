package com.example.virtualwallet.controllers.rest;

import com.example.virtualwallet.exceptions.AuthorizationException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.helpers.AuthenticationHelper;
import com.example.virtualwallet.helpers.TransferMapper;
import com.example.virtualwallet.models.SpendingCategory;
import com.example.virtualwallet.models.Transfer;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.TransferRequestDto;
import com.example.virtualwallet.services.contracts.BankService;
import com.example.virtualwallet.services.contracts.SpendingCategoryService;
import com.example.virtualwallet.services.contracts.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import static com.example.virtualwallet.utils.Messages.SUCCESS_TRANSFER;

@RestController
@RequestMapping("/api/transfers")
public class BankRestController {

    private final BankService bankService;
    private final WalletService walletService;
    private final SpendingCategoryService spendingCategoryService;
    private final TransferMapper transferMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public BankRestController(BankService bankService,
                              WalletService walletService,
                              SpendingCategoryService spendingCategoryService,
                              TransferMapper transferMapper,
                              AuthenticationHelper authenticationHelper) {
        this.bankService = bankService;
        this.walletService = walletService;
        this.spendingCategoryService = spendingCategoryService;
        this.transferMapper = transferMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @PostMapping("/{walletId}/out")
    public String transferMoneyOut(@RequestHeader HttpHeaders headers,
                                   @PathVariable int walletId,
                                   @Valid @RequestBody TransferRequestDto requestDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet senderWallet = walletService.getWalletById(walletId, user.getId());
            SpendingCategory existingSpendingCategory=spendingCategoryService
                    .getSpendingCategoryByName(requestDto.getSpendingCategory());
            Transfer transferOut=transferMapper.fromDtoMoneyOut(senderWallet,requestDto,
                    existingSpendingCategory);
            bankService.transferMoneyOut( transferOut,senderWallet, user);
            return SUCCESS_TRANSFER;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }//Todo add check to MVC for INVALID_REQUEST
    }

    @PostMapping("/{walletId}/in")
    public String transferMoneyIn(@RequestHeader HttpHeaders headers,
                                  @PathVariable int walletId,
                                  @Valid @RequestBody TransferRequestDto requestDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Wallet receiverWallet = walletService.getWalletById(walletId, user.getId());
            SpendingCategory existingSpendingCategory=spendingCategoryService
                    .getSpendingCategoryByName(requestDto.getSpendingCategory());
            Transfer transferIn=transferMapper.fromDtoMoneyIn(receiverWallet,requestDto,
                    existingSpendingCategory);
            bankService.transferMoneyIn( transferIn,receiverWallet, user);
            return SUCCESS_TRANSFER;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
            // return new TransferResponse(false, "Invalid request data");
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, e.getMessage());
        }//Todo add check to MVC for INVALID_REQUEST
    }

}








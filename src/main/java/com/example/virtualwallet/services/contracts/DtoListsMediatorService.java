package com.example.virtualwallet.services.contracts;

import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.*;

import java.sql.Date;
import java.util.List;

public interface DtoListsMediatorService {

    PaginatedTransactionListDto getPresentableTransactionsWithPagination(Date startDate,
                                                                         Date endDate,
                                                                         String loggedUserUsername,
                                                                         String counterpartyUsername,
                                                                         String amount,
                                                                         String date,
                                                                         int page,
                                                                         int pageSize);

    PaginatedTransactionListDtoForAdmin getPresentableTransactionsForAdminWithPagination(Date startDate,
                                                                                         Date endDate,
                                                                                         String senderUsername,
                                                                                         String recipientUsername,
                                                                                         String amount,
                                                                                         String date,
                                                                                         int page,
                                                                                         int pageSize);

    PaginatedRecipientListDto getRecipientsWithPagination(String contactType,
                                                          String contactInformation,
                                                          int page,
                                                          int pageSize);

    PaginatedUserListDto getPresentableUsersWithPagination(String contactType,
                                                           String contactInformation,
                                                           int page,
                                                           int pageSize);

    List<PresentableCardDto> getPresentableCardDtos(int userId, User user);

    List<PresentableWalletDto> getPresentableWalletDtos(String ownerUsername);
}
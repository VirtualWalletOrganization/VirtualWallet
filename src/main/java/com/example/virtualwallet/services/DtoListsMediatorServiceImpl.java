//package com.example.virtualwallet.services;
//
//import com.example.virtualwallet.exceptions.EntityNotFoundException;
//import com.example.virtualwallet.helpers.TransactionMapper;
//import com.example.virtualwallet.helpers.UserMapper;
//import com.example.virtualwallet.models.Card;
//import com.example.virtualwallet.models.Transaction;
//import com.example.virtualwallet.models.User;
//import com.example.virtualwallet.models.Wallet;
//import com.example.virtualwallet.models.dtos.*;
//import com.example.virtualwallet.services.contracts.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.sql.Date;
//import java.time.Instant;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static com.example.virtualwallet.utils.Messages.*;
//
//@Service
//public class DtoListsMediatorServiceImpl implements DtoListsMediatorService {
//
//    private UserService userService;
//    private TransactionMapper transactionDtoMapper;
//    private UserMapper userDtoMapper;
//    private TransactionService transactionService;
//    private WalletService walletService;
//    private CardService cardService;
//
//    @Value("${admin.transactions.recipientDoesNotExist}")
//    private String recipientDoesNotExist;
//
//    @Value("${admin.transactions.senderDoesNotExist}")
//    private String senderDoesNotExist;
//
//    @Value("${admin.transactions.recipientOrSenderDoesNotExist}")
//    private String senderOrRecipientDoesNotExist;
//
//    @Autowired
//    public DtoListsMediatorServiceImpl(UserService userService,
//                                       TransactionMapper transactionDtoMapper,
//                                       TransactionService transactionService) {
//        this.userService = userService;
//        this.transactionDtoMapper = transactionDtoMapper;
//        this.transactionService = transactionService;
//    }
//
//    @Override
//    public PaginatedTransactionListDto getPresentableTransactionsWithPagination(Date startDate,
//                                                                                Date endDate,
//                                                                                String loggedUserUsername,
//                                                                                String counterpartyUsername,
//                                                                                String amount,
//                                                                                String date,
//                                                                                int page,
//                                                                                int pageSize) {
//        User user = userService.getByUsername(loggedUserUsername);
//        Instant currentTimeInstant = ZonedDateTime.now().toInstant();
//
//        if (startDate == null) {
//            startDate = Date.valueOf((currentTimeInstant.minus(DAYS_IN_A_MONTH)).toString().substring(0, 10));
//        }
//
//        if (endDate == null) {
//            endDate = Date.valueOf(currentTimeInstant.toString().substring(0, 10));
//        }
//
//        Pageable pageable = PageRequest.of(page - 1, pageSize);
//        List<String> sortCriteria = getSortingCriteriaList(amount, date);
//        Page<Transaction> transactions = getTransactionsByCriteria(startDate, endDate, counterpartyUsername, direction, user, sortCriteria, pageable);
//        List<PresentableTransactionDto> transactionListDto = new ArrayList<>();
//
////        for (Transaction transaction : transactions) {
////            PresentableTransactionDto presentableTransactionDto = transactionDtoMapper.toDto(transaction);
////            transactionListDto.add(presentableTransactionDto);
////        }
//
//        return populatePaginatedTransactionListDto(
//                startDate, endDate, transactionListDto, sortCriteria, page,
//                pageSize, transactions.getNumber(), transactions.getTotalPages());
//    }
//
//    @Override
//    public PaginatedTransactionListDtoForAdmin getPresentableTransactionsForAdminWithPagination(Date startDate,
//                                                                                                Date endDate,
//                                                                                                String senderUsername,
//                                                                                                String recipientUsername,
//                                                                                                String amount,
//                                                                                                String date,
//                                                                                                int page,
//                                                                                                int pageSize) {
//        Instant currentTimeInstant = ZonedDateTime.now().toInstant();
//
//        if (startDate == null) {
//            startDate = Date.valueOf((currentTimeInstant.minus(DAYS_IN_A_MONTH)).toString().substring(0, 10));
//        }
//
//        if (endDate == null) {
//            endDate = Date.valueOf(currentTimeInstant.toString().substring(0, 10));
//        }
//
//        Pageable pageable = PageRequest.of(page - 1, pageSize);
//        List<String> sortCriteria = getSortingCriteriaList(amount, date);
//        Page<Transaction> transactions = getAdminTransactionsByCriteria(startDate, endDate, senderUsername, recipientUsername, sortCriteria, pageable);
//        List<PresentableTransactionDto> transactionListDto = new ArrayList<>();
//
//        for (Transaction transaction : transactions) {
//            PresentableTransactionDto presentableTransactionDto = transactionDtoMapper.toDto(transaction);
//            transactionListDto.add(presentableTransactionDto);
//        }
//
//        return populatePaginatedTransactionListDtoForAdmins(
//                startDate, endDate, transactionListDto, sortCriteria, page,
//                pageSize, transactions.getNumber(), transactions.getTotalPages());
//    }
//
////    @Override
////    public PaginatedRecipientListDto getRecipientsWithPagination(String contactType, String contactInformation, int page, int pageSize) {
////        Page<User> recipients = userService.findUsersByContactType(PageRequest.of(page - 1, pageSize), contactType, contactInformation);
////        List<RecipientDto> listOfUserDtos = new ArrayList<>();
////        for (User user : recipients) {
////            listOfUserDtos.add(userDtoMapper.toRecipientDto(user));
////        }
////        return getPaginatedRecipientListDto(listOfUserDtos, page, pageSize, recipients.getNumber() + 1, recipients.getTotalPages());
////    }
//
////    @Override
////    public PaginatedUserListDto getPresentableUsersWithPagination(String contactType, String contactInformation, int page, int pageSize) {
////        Page<User> userPage;
////        if (!contactType.equals(DEFAULT_EMPTY_VALUE)) {
////            userPage = userService.getAll(PageRequest.of(page - 1, pageSize), contactType, contactInformation);
////        } else {
////            userPage = userService.getAll(PageRequest.of(page - 1, pageSize));
////        }
////        List<PresentableUserDto> listOfUserDtos = new ArrayList<>();
////        for (User user : userPage) {
////            PresentableUserDto presentableUserDto = userDtoMapper.toDto(user);
////            listOfUserDtos.add(presentableUserDto);
////        }
////        return ModelFactory.getPaginatedUserListDto(
////                listOfUserDtos, page, pageSize,
////                userPage.getNumber(), userPage.getTotalPages());
////    }
//
//    @Override
//    public List<PresentableCardDto> getPresentableCardDtos(int userId, User user) {
//        List<Card> list = cardService.getAllCardsByUserId(userId, user);
//        List<PresentableCardDto> presentableCardDtos = new ArrayList<>();
//
////        for (Card card : list) {
////            PresentableCardDto cardDto = paymentInstrumentDtoMapper.toDto(card);
////            presentableCardDtos.add(cardDto);
////        }
//
//        return presentableCardDtos;
//    }
//
//    @Override
//    public List<PresentableWalletDto> getPresentableWalletDtos(String ownerUsername) {
//        User user = userService.getByUsername(ownerUsername);
//        List<Wallet> userWallets = walletService.getAll(user);
//        List<PresentableWalletDto> presentableWalletDtoList = new ArrayList<>();
//
////        for (Wallet wallet : userWallets) {
////            PresentableWalletDto walletDto = paymentInstrumentDtoMapper.toDto(wallet, user);
////            presentableWalletDtoList.add(walletDto);
////        }
//
//        return presentableWalletDtoList;
//    }
//
//    @Autowired
//    public void setUserDtoMapper(UserMapper userDtoMapper) {
//        this.userDtoMapper = userDtoMapper;
//    }
//
////    @Autowired
////    public void setPaymentInstrumentDtoMapper(PaymentInstrumentDtoMapper paymentInstrumentDtoMapper) {
////        this.paymentInstrumentDtoMapper = paymentInstrumentDtoMapper;
////    }
//
//    @Autowired
//    public void setWalletService(WalletService walletService) {
//        this.walletService = walletService;
//    }
//
//    @Autowired
//    public void setCardService(CardService cardService) {
//        this.cardService = cardService;
//    }
//
//    private Page<Transaction> getTransactionsByCriteria(Date startDate, Date endDate, String counterparty, User user,
//                                                        List<String> sortBy, Pageable pageable) {
//        Page<Transaction> transactions = new PageImpl<>(new ArrayList<>(), pageable, 0);
//        if (counterparty == null) {
//            switch (direction) {
//                case INCOMING:
//                    transactions = transactionService.filterIncoming(startDate, endDate, user, Optional.empty(), sortBy, pageable, false);
//                    break;
//                case OUTGOING:
//                    transactions = transactionService.filterOutgoing(startDate, endDate, user, Optional.empty(), sortBy, pageable, false);
//                    break;
//                case ALL:
//                    transactions = transactionService.filterForUser(startDate, endDate, user, sortBy, pageable);
//            }
//        } else {
//            User otherUser = userService.getByUsername(counterparty);
//            switch (direction) {
//                case INCOMING:
//                    transactions = transactionService.filterIncoming(startDate, endDate, user, Optional.of(otherUser), sortBy, pageable, false);
//                    break;
//                case OUTGOING:
//                    transactions = transactionService.filterOutgoing(startDate, endDate, user, Optional.of(otherUser), sortBy, pageable, false);
//                    break;
//                case ALL:
//                    transactions = transactionService.filterForUserWithCounterparty(startDate, endDate, user, otherUser, sortBy, pageable);
//            }
//        }
//
//        return transactions;
//    }
//
//    private Page<Transaction> getAdminTransactionsByCriteria(Date startDate, Date endDate, String senderUsername, String recipientUsername,
//                                                             List<String> sortCriteria, Pageable pageable) {
//        Page<Transaction> transactions;
//
//        if (recipientUsername == null) {
//            if (senderUsername == null) {
//                transactions = transactionService.filter(startDate, endDate, Optional.empty(), Optional.empty(), sortCriteria, pageable, false);
//            } else {
//                try {
//                    User sender = userService.getByUsername(senderUsername);
//                    transactions = transactionService.filter(startDate, endDate, Optional.of(sender), Optional.empty(), sortCriteria, pageable, false);
//                } catch (EntityNotFoundException e) {
//                    throw new EntityNotFoundException(senderDoesNotExist);
//                }
//            }
//        } else {
//            if (senderUsername == null) {
//                try {
//                    User recipient = userService.getByUsername(recipientUsername);
//                    transactions = transactionService.filter(startDate, endDate, Optional.empty(), Optional.of(recipient), sortCriteria, pageable, false);
//                } catch (EntityNotFoundException e) {
//                    throw new EntityNotFoundException(recipientDoesNotExist);
//                }
//            } else {
//                try {
//                    User recipient = userService.getByUsername(recipientUsername);
//                    User sender = userService.getByUsername(senderUsername);
//                    transactions = transactionService.filter(startDate, endDate, Optional.of(sender), Optional.of(recipient), sortCriteria, pageable, false);
//                } catch (EntityNotFoundException e) {
//                    throw new EntityNotFoundException(senderOrRecipientDoesNotExist);
//                }
//            }
//        }
//
//        return transactions;
//    }
//
//    private static List<String> getSortingCriteriaList(String amount, String date) {
//        List<String> sortCriteria = new ArrayList<>();
//
//        if (!amount.equals(DEFAULT_EMPTY_VALUE)) {
//            sortCriteria.add("amount." + amount);
//        }
//
//        if (!date.equals(DEFAULT_EMPTY_VALUE)) {
//            sortCriteria.add("date." + date);
//        }
//
//        if (sortCriteria.isEmpty()) {
//            sortCriteria = Collections.singletonList("date.desc");
//
//        }
//        return sortCriteria;
//    }
//
//    private static PaginatedTransactionListDto populatePaginatedTransactionListDto(Date startDate,
//                                                                                   Date endDate,
//                                                                                   List<PresentableTransactionDto> transactionDtos,
//                                                                                   List<String> sortCriteria,
//                                                                                   int page,
//                                                                                   int pageSize,
//                                                                                   int currentPage,
//                                                                                   int lastPage) {
//        PaginatedTransactionListDto paginatedTransactionListDto = new PaginatedTransactionListDto();
//        paginatedTransactionListDto.setStartDate(startDate);
//        paginatedTransactionListDto.setEndDate(endDate);
//        paginatedTransactionListDto.setList(transactionDtos);
//        paginatedTransactionListDto.setSortCriteria(sortCriteria);
//        addPagination(paginatedTransactionListDto, page, pageSize, currentPage, lastPage);
//        return paginatedTransactionListDto;
//    }
//
//    private static <T extends PaginatedList> void addPagination(T t, int page, int pageSize, int currentPage, int lastPage) {
//        t.setPage(page);
//        t.setPageSize(pageSize);
//        t.setTotalPages(lastPage);
//
//        if (lastPage > 0) {
//            int beginIndex;
//            int endIndex;
//
//            if (currentPage + PAGE_WINDOW_SIZE <= lastPage) {
//                beginIndex = Math.max(1, currentPage);
//                endIndex = beginIndex + PAGE_WINDOW_SIZE - 1;
//            } else {
//                endIndex = lastPage;
//                beginIndex = Math.max(1, endIndex - PAGE_WINDOW_SIZE + 1);
//            }
//
//            t.setBeginIndex(beginIndex);
//            t.setEndIndex(endIndex);
//        }
//    }
//
//    private static PaginatedTransactionListDtoForAdmin populatePaginatedTransactionListDtoForAdmins(Date startDate,
//                                                                                                    Date endDate,
//                                                                                                    List<PresentableTransactionDto> list,
//                                                                                                    List<String> sortCriteria,
//                                                                                                    int page,
//                                                                                                    int pageSize,
//                                                                                                    int currentPage,
//                                                                                                    int lastPage) {
//        PaginatedTransactionListDtoForAdmin dtoForAdmin = new PaginatedTransactionListDtoForAdmin();
//        dtoForAdmin.setStartDate(startDate);
//        dtoForAdmin.setEndDate(endDate);
//        dtoForAdmin.setList(list);
//        dtoForAdmin.setSortCriteria(sortCriteria);
//        dtoForAdmin.setPage(page);
//        dtoForAdmin.setPageSize(pageSize);
//        dtoForAdmin.setTotalPages(lastPage);
//        addPagination(dtoForAdmin, page, pageSize, currentPage, lastPage);
//        return dtoForAdmin;
//    }
//
//    private static PaginatedRecipientListDto getPaginatedRecipientListDto(List<RecipientDto> list,
//                                                                          int page,
//                                                                          int pageSize,
//                                                                          int currentPage,
//                                                                          int lastPage) {
//        PaginatedRecipientListDto paginatedUserListDto = new PaginatedRecipientListDto();
//        paginatedUserListDto.setList(list);
//        addPagination(paginatedUserListDto, page, pageSize, currentPage, lastPage);
//        return paginatedUserListDto;
//    }
//}
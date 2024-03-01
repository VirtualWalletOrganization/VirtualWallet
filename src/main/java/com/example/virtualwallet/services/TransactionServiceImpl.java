package com.example.virtualwallet.services;

import com.example.virtualwallet.exceptions.EntityNotFoundException;
import com.example.virtualwallet.exceptions.InsufficientBalanceException;
import com.example.virtualwallet.models.Transaction;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.enums.Direction;
import com.example.virtualwallet.models.enums.Status;
import com.example.virtualwallet.models.enums.TransactionType;
import com.example.virtualwallet.repositories.contracts.TransactionRepository;
import com.example.virtualwallet.services.contracts.TransactionService;
import com.example.virtualwallet.services.contracts.UserService;
import com.example.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
        private final TransactionRepository transactionRepository;
        private final UserService userService;
        private final WalletService walletService;

      @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, UserService userService, WalletService walletService) {
        this.transactionRepository = transactionRepository;
          this.userService = userService;
          this.walletService = walletService;
      }

    @Override
        public List<Transaction> getAllTransfers() {
            return transactionRepository.getAllTransaction()
                    .orElseThrow(() -> new EntityNotFoundException("Transactions"));
        }

        @Override
        public Transaction getTransactionById(int transactionId) {
            return transactionRepository.getTransactionById(transactionId)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction", "id", String.valueOf(transactionId)));
        }



        @Override
        public void createTransaction(int senderUserId, int recipientUserId, int walletId, double amount) {
            User sender = userService.getById(senderUserId);
            User recipient = userService.getById(recipientUserId);
            Wallet walletSender = sender.getWallets().stream()
                    .filter(w -> w.getId() == walletId)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));
            Wallet walletReceiver = sender.getWallets().stream()
                    .filter(Wallet::getDefault)
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(walletId)));

            if (walletSender.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                throw new InsufficientBalanceException("Insufficient balance in sender's wallet.");
                //TODO implement check for overdraft
            }
        //ToDO move to dto
            Transaction transaction = new Transaction();
        transaction.setWalletSender(walletSender);
        transaction.setWalletReceiver(walletReceiver);
        transaction.setAmount(amount);
        transaction.setCurrency(walletSender);
        transaction.setDirection(Direction.OUTGOING);
        transaction.setDate(new Date());
        transaction.setStatus(Status.PENDING);
        transaction.setDescription("Transaction from " + sender.getUsername() + " to " + recipient.getUsername());
        transaction.setTransactionType(TransactionType.SINGLE);
            transactionRepository.update(transaction);
        }

        @Override
        public void confirmTransaction(int transactionId, int senderUserId, int recipientId, double amount) {
            Transaction transaction = getTransactionById(transactionId);
            transaction.setStatus(Status.COMPLETED);
            transactionRepository.update(transaction);
            Wallet walletSender = walletService.getByCreatorId(senderUserId).stream()
                    .filter(w -> w.getId() == transaction.getWalletSender().getId())
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(transaction.getWalletSender().getId())));
            Wallet walletReceiver = walletService.getByCreatorId(recipientId).stream()
                    .filter(w -> w.getId() == transaction.getWalletReceiver().getId())
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Wallet", "id", String.valueOf(transaction.getWalletReceiver().getId())));

            User sender = userService.getById(senderUserId);
            User recipient = userService.getById(recipientId);
            walletSender.setBalance(walletSender.getBalance().subtract(BigDecimal.valueOf(amount)));
            walletReceiver.setBalance(walletSender.getBalance().add(BigDecimal.valueOf(amount)));
            userService.updateUser(sender, recipient);
        }
    @Override
    public void updateTransaction(Transaction transaction) {
        //       Todo move to dto
//        public void editTransfer(int transferId, double newAmount) {
//            Transfer transfer = getTransferById(transferId);
//            transfer.setAmount(newAmount);
//            transferRepository.update(transfer);
//        }
        transactionRepository.update(transaction);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {

          transactionRepository.delete(transaction);
    }




}

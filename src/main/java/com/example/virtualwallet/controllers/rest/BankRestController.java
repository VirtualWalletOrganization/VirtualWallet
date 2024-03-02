//package com.example.virtualwallet.controllers.rest;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/transfer")
//public class BankRestController {
//    @PostMapping
//    public TransferResponse transferMoney(@RequestBody TransferRequest request) {
//
//        if (isValidRequest(request)) {
//            // Check if sender has sufficient balance (dummy check)
//            double senderBalance = getAccountBalance(request.getSenderAccount());
//            if (senderBalance >= request.getAmount()) {
//                // Deduct amount from sender's account
//                updateAccountBalance(request.getSenderAccount(), senderBalance - request.getAmount());
//                // Add amount to recipient's account
//                updateAccountBalance(request.getRecipientAccount(), getAccountBalance(request.getRecipientAccount()) + request.getAmount());
//                return new TransferResponse(true, "Transfer successful");
//            } else {
//                return new TransferResponse(false, "Insufficient balance");
//            }
//        } else {
//            return new TransferResponse(false, "Invalid request data");
//        }
//    }
//
//    private boolean isValidRequest(TransferRequest request) {
//        // Add validation logic here
//        return request != null && request.getSenderAccount() != null && request.getRecipientAccount() != null && request.getAmount() > 0;
//    }
//
//    private double getAccountBalance(String accountId) {
//        // Dummy function to get account balance
//        // Replace with actual logic to fetch balance from database or external API
//        return 10000; // Dummy balance
//    }
//
//    private void updateAccountBalance(String accountId, double newBalance) {
//        // Dummy function to update account balance
//        // Replace with actual logic to update balance in database or external API
//    }
//}
//
//class TransferRequest {
//    private String senderAccount;
//    private String recipientAccount;
//    private double amount;
//
//    // Getters and setters
//}
//
//class TransferResponse {
//    private boolean success;
//    private String message;
//
//    // Constructor, getters and setters
//}
//
//
//}

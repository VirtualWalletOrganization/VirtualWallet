package com.example.virtualwallet.models.dtos;

public class TransactionDto {

    private String senderWallet;
    private String receiverWallet;
    private String currency;

    public TransactionDto() {
    }

    public String getSenderWallet() {
        return senderWallet;
    }

    public void setSenderWallet(String senderWallet) {
        this.senderWallet = senderWallet;
    }

    public String getReceiverWallet() {
        return receiverWallet;
    }

    public void setReceiverWallet(String receiverWallet) {
        this.receiverWallet = receiverWallet;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
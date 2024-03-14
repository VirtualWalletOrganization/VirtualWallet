package com.example.virtualwallet.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class TransactionFilterOptions {

    private Optional<String> sender;
    private Optional<String> recipient;
    private Optional<BigDecimal> amount;
    private Optional<String> currency;
    private Optional<LocalDateTime> creationTime;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public TransactionFilterOptions() {
        this(null,null,null,null,null,null,null );
    }

    public TransactionFilterOptions(
            String sender,
            String recipient,
            BigDecimal amount,
            String currency,
            LocalDateTime creationTime,
            String sortBy,
            String sortOrder) {
        this.sender = Optional.ofNullable(sender);
        this.recipient = Optional.ofNullable(recipient);
        this.amount = Optional.ofNullable(amount);
        this.currency = Optional.ofNullable(currency);
        this.creationTime = Optional.ofNullable(creationTime);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getSender() {
        return sender;
    }

    public Optional<String> getRecipient() {
        return recipient;
    }

    public Optional<BigDecimal> getAmount() {
        return amount;
    }

    public Optional<String> getCurrency() {
        return currency;
    }

    public Optional<LocalDateTime> getCreationTime() {
        return creationTime;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
package com.example.virtualwallet.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

public class TransactionHistoryFilterOptions {
    private Optional<Timestamp> startDate;
    private Optional<Timestamp> endDate;
    private Optional<String> counterparty;
    private Optional<BigDecimal> amount;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;


    public TransactionHistoryFilterOptions() {
        this(null, null,null, null, null, null);
    }

    public TransactionHistoryFilterOptions(
            Timestamp startDate,
            Timestamp endDate,
            String counterparty,
            BigDecimal amount,
            String sortBy,
            String sortOrder) {
        this.startDate = Optional.ofNullable(startDate);
        this.endDate = Optional.ofNullable(endDate);
        this.counterparty = Optional.ofNullable(counterparty);
        this.amount = Optional.ofNullable(amount);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<Timestamp> getStartDate() {
        return startDate;
    }

    public Optional<Timestamp> getEndDate() {
        return endDate;
    }

    public Optional<String> getCounterparty() {
        return counterparty;
    }

    public Optional<BigDecimal> getAmount() {
        return amount;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}

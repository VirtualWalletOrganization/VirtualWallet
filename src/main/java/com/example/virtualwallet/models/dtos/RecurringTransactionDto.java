package com.example.virtualwallet.models.dtos;

import java.time.LocalDate;

public class RecurringTransactionDto extends TransactionDto {

    private String frequency;
    private LocalDate startDate;

    private LocalDate endDate;

    public RecurringTransactionDto() {
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}


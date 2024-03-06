package com.example.virtualwallet.models.dtos;

import java.time.LocalDate;
import java.util.Date;

public class RecurringTransactionDto extends TransactionDto {

    private String interval;
    private LocalDate startDate;
    private LocalDate endDate;

    public RecurringTransactionDto() {
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
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


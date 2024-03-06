package com.example.virtualwallet.models.dtos;

import java.util.Date;

public class RecurringTransactionDto extends TransactionDto {

    private String interval;
    private Date startDate;
    private Date endDate;

    public RecurringTransactionDto() {
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}


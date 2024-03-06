package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Interval;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "recurring_transactions")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class RecurringTransaction extends Transaction {
    @Enumerated(EnumType.STRING)
    @Column(name = "intervals")
    private Interval intervals;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    public RecurringTransaction() {

    }


    public Interval getIntervals() {
        return intervals;
    }

    public void setIntervals(Interval intervals) {
        this.intervals = intervals;
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
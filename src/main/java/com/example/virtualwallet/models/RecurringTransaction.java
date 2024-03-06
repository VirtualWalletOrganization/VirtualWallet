package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Interval;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recurring_transactions")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class RecurringTransaction extends Transaction {
    @Enumerated(EnumType.STRING)
    @Column(name = "intervals")
    private Interval intervals;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public RecurringTransaction() {

    }


    public Interval getIntervals() {
        return intervals;
    }

    public void setIntervals(Interval intervals) {
        this.intervals = intervals;
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
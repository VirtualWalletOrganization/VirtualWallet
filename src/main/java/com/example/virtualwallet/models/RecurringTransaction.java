package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Interval;
import com.example.virtualwallet.models.enums.Status;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction extends Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurring_transaction_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "intervals")
    private Interval intervals;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    public RecurringTransaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
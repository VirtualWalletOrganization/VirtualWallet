package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Frequency;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "recurring_transactions")
@PrimaryKeyJoinColumn(name = "transaction_id")
public class RecurringTransaction extends Transaction {

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency")
    private Frequency frequency;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public RecurringTransaction() {
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
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
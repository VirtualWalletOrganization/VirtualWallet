package com.example.virtualwallet.models.dtos;

import java.sql.Timestamp;
import java.util.List;

public class TransactionHistoryDto {
    private Timestamp startDate;

    private Timestamp endDate;

    String counterpartyUsername;

    private List<TransactionDto> list;

    private List<String> sortCriteria;

    public TransactionHistoryDto() {
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public String getCounterpartyUsername() {
        return counterpartyUsername;
    }

    public void setCounterpartyUsername(String counterpartyUsername) {
        this.counterpartyUsername = counterpartyUsername;
    }

    public List<TransactionDto> getList() {
        return list;
    }

    public void setList(List<TransactionDto> list) {
        this.list = list;
    }

    public List<String> getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(List<String> sortCriteria) {
        this.sortCriteria = sortCriteria;
    }
}

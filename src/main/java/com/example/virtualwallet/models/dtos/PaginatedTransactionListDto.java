package com.example.virtualwallet.models.dtos;

import java.sql.Date;
import java.util.List;

public class PaginatedTransactionListDto extends PaginatedList {

    private List<PresentableTransactionDto> list;

    private List<String> sortCriteria;

    private Date startDate;

    private Date endDate;

    private String counterPartyUsername;

    public PaginatedTransactionListDto() {
    }

    public List<PresentableTransactionDto> getList() {
        return list;
    }


    public void setList(List<PresentableTransactionDto> list) {
        this.list = list;
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

    public String getCounterPartyUsername() {
        return counterPartyUsername;
    }

    public void setCounterPartyUsername(String counterPartyUsername) {
        this.counterPartyUsername = counterPartyUsername;
    }

    public List<String> getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(List<String> sortCriteria) {
        this.sortCriteria = sortCriteria;
    }
}
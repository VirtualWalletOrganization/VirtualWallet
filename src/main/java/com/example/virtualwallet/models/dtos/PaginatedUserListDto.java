package com.example.virtualwallet.models.dtos;

import java.util.List;

public class PaginatedUserListDto extends PaginatedList {

    private List<PresentableUserDto> list;

    public PaginatedUserListDto() {
    }

    public List<PresentableUserDto> getList() {
        return list;
    }

    public void setList(List<PresentableUserDto> list) {
        this.list = list;
    }
}

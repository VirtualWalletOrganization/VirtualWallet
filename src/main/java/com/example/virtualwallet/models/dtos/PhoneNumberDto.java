package com.example.virtualwallet.models.dtos;

public class PhoneNumberDto {

    private String phoneNumber;

    public PhoneNumberDto() {

    }

    public PhoneNumberDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
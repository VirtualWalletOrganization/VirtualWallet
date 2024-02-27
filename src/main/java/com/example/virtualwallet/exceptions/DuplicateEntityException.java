package com.example.virtualwallet.exceptions;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String type, String attribute, String value) {
        super(String.format("%s with %s %s already exists.", type, attribute, value));
    }

    public DuplicateEntityException(String type, String attribute, String value, String message) {
        super(String.format("%s with %s %s %s", type, attribute, value, message));
    }
}
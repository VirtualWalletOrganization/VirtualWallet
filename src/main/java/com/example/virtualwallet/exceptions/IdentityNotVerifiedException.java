package com.example.virtualwallet.exceptions;

public class IdentityNotVerifiedException extends RuntimeException {

    public IdentityNotVerifiedException(String message) {
        super(message);
    }
}
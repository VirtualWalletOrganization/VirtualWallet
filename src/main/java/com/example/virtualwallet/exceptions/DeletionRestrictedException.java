package com.example.virtualwallet.exceptions;

public class DeletionRestrictedException extends  RuntimeException{
    public DeletionRestrictedException(String message) {
        super(message);
    }
}

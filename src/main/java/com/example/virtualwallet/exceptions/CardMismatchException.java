package com.example.virtualwallet.exceptions;

public class CardMismatchException extends RuntimeException{

    public CardMismatchException(String message) {
        super(message);
    }
}
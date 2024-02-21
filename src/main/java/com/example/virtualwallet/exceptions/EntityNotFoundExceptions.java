package com.example.virtualwallet.exceptions;

public class EntityNotFoundExceptions extends RuntimeException {

    public EntityNotFoundExceptions(String type, int id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundExceptions(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }
}
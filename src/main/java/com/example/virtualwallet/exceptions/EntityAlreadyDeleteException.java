package com.example.virtualwallet.exceptions;

public class EntityAlreadyDeleteException extends RuntimeException{
    public EntityAlreadyDeleteException(String type, String attribute, String value) {
        super(String.format("%s with %s %s has already been deleted.", type, attribute, value));
    }

    public EntityAlreadyDeleteException( String attribute, String message) {
        super(String.format("User with %s %s has already been deleted. If you want to reactivate your account, please fill in the form for registration with your personal information and your account will be reactivated.",attribute,message));
    }
}
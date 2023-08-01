package com.paymybuddy.exceptions;

public class IbanAlreadyExistsException extends RuntimeException {

    public IbanAlreadyExistsException(String message) {
        super(message);
    }
}

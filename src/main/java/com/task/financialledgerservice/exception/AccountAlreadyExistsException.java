package com.task.financialledgerservice.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}

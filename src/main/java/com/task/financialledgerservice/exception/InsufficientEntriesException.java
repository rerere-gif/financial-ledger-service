package com.task.financialledgerservice.exception;

public class InsufficientEntriesException extends RuntimeException {

    public InsufficientEntriesException(String message) {
        super(message);
    }
}

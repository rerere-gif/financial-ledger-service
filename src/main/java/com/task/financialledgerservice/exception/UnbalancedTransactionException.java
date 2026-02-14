package com.task.financialledgerservice.exception;

import java.util.List;

public class UnbalancedTransactionException extends RuntimeException {
    private final List<String> errors;

    public UnbalancedTransactionException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
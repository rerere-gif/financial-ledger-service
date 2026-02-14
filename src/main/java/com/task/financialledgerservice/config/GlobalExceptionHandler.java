package com.task.financialledgerservice.config;

import com.task.financialledgerservice.exception.*;
import com.task.financialledgerservice.exception.AccountAlreadyExistsException;
import com.task.financialledgerservice.exception.AccountNotFoundException;
import com.task.financialledgerservice.exception.InsufficientEntriesException;
import com.task.financialledgerservice.exception.UnbalancedTransactionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // @RestControllerAdvice = @ControllerAdvice + @ResponseBody для всех методов обработки
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<String> handleAccountAlreadyExists(AccountAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
    }

    @ExceptionHandler(InsufficientEntriesException.class)
    public ResponseEntity<String> handleInsufficientEntries(InsufficientEntriesException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnbalancedTransactionException.class)
    public ResponseEntity<Object> handleUnbalancedTransaction(UnbalancedTransactionException ex) {
        return new ResponseEntity<>(ex.getErrors(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<String> handleTransactionNotFound(TransactionNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        // В реальных приложениях не стоит возвращать ex.getMessage() напрямую, это потенциальная уязвимость
        // Лучше логировать и возвращать общий текст ошибки
        System.err.println("An unexpected error occurred: " + ex.getMessage());
        ex.printStackTrace(); // Логируем стек вызова
        return new ResponseEntity<>("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
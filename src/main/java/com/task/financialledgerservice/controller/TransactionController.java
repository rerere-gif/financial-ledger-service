package com.task.financialledgerservice.controller;

import com.task.financialledgerservice.dto.CreateTransactionRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody CreateTransactionRequestDto requestDto) {
        TransactionDto createdTransaction = transactionService.createTransaction(requestDto);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        TransactionDto transaction = transactionService.getTransactionById(id);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}
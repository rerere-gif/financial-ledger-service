package com.task.financialledgerservice.controller;

import com.task.financialledgerservice.dto.AccountDto;
import com.task.financialledgerservice.dto.CreateAccountRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.service.AccountService;
import com.task.financialledgerservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid CreateAccountRequestDto requestDto) {
        AccountDto createdAccount = accountService.createAccount(requestDto.getName(), requestDto.getType());
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        AccountDto account = accountService.getAccountById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactionsByAccountId(@PathVariable Long id) {
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(id);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
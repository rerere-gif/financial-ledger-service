package com.task.financialledgerservice.service;

import com.task.financialledgerservice.dto.CreateTransactionRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import java.util.List;

public interface TransactionService {
    TransactionDto createTransaction(CreateTransactionRequestDto requestDto);
    TransactionDto getTransactionById(Long id);
    List<TransactionDto> getTransactionsByAccountId(Long accountId);
}
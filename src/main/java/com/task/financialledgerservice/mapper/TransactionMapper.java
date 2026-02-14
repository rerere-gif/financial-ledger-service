package com.task.financialledgerservice.mapper;

import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.dto.TransactionEntryDto;
import com.task.financialledgerservice.model.Transaction;
import com.task.financialledgerservice.model.TransactionEntry;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class TransactionMapper {

    public TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionDate(transaction.getTransactionDate());
        dto.setEntries(transaction.getEntries().stream()
                .map(this::convertEntryToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    private TransactionEntryDto convertEntryToDto(TransactionEntry entry) {
        TransactionEntryDto dto = new TransactionEntryDto();
        dto.setId(entry.getId());
        dto.setAccountId(entry.getAccount().getId());
        dto.setAccountName(entry.getAccount().getName());
        dto.setType(entry.getType());
        dto.setAmount(entry.getAmount());
        return dto;
    }
}
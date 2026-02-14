package com.task.financialledgerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long id;
    private String description;
    private OffsetDateTime transactionDate;
    private List<TransactionEntryDto> entries;
}
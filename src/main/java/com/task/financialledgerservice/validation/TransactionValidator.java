package com.task.financialledgerservice.validation;

import com.task.financialledgerservice.dto.CreateTransactionRequestDto;
import com.task.financialledgerservice.exception.InsufficientEntriesException;
import com.task.financialledgerservice.exception.UnbalancedTransactionException;
import com.task.financialledgerservice.model.enums.EntryType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class TransactionValidator {

    public void validateCreateTransactionRequest(CreateTransactionRequestDto requestDto) {
        List<String> errors = new ArrayList<>();

        if (requestDto.getEntries() == null || requestDto.getEntries().size() < 2) {
            errors.add("A transaction must have at least two entries.");
        }

        if (!errors.isEmpty()) {
            throw new InsufficientEntriesException("Validation failed: " + String.join(", ", errors));
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (CreateTransactionRequestDto.Entry entryDto : requestDto.getEntries()) {
            if (entryDto.getType() == EntryType.DEBIT) {
                totalDebit = totalDebit.add(entryDto.getAmount());
            } else {
                totalCredit = totalCredit.add(entryDto.getAmount());
            }
        }

        totalDebit = totalDebit.setScale(2, RoundingMode.HALF_UP);
        totalCredit = totalCredit.setScale(2, RoundingMode.HALF_UP);

        if (totalDebit.compareTo(totalCredit) != 0) {
            errors.add("Total debits (" + totalDebit + ") do not equal total credits (" + totalCredit + ").");
            throw new UnbalancedTransactionException("Transaction is not balanced.", errors);
        }
    }
}
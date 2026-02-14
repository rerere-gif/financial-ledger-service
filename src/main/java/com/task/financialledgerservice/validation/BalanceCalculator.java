package com.task.financialledgerservice.validation;

import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.model.TransactionEntry;
import com.task.financialledgerservice.repository.TransactionEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BalanceCalculator {

    private final TransactionEntryRepository transactionEntryRepository;

    public BigDecimal calculateBalance(Account account) {
        List<TransactionEntry> entries = transactionEntryRepository.findByAccountId(account.getId());
        BigDecimal balance = BigDecimal.ZERO;

        for (TransactionEntry entry : entries) {
            if (account.getType() == AccountType.ASSET || account.getType() == AccountType.EXPENSE) {
                if (entry.getType() == com.task.financialledgerservice.model.enums.EntryType.DEBIT) {
                    balance = balance.add(entry.getAmount()).setScale(2, RoundingMode.HALF_UP);
                } else {
                    balance = balance.subtract(entry.getAmount()).setScale(2, RoundingMode.HALF_UP);
                }
            } else {
                if (entry.getType() == com.task.financialledgerservice.model.enums.EntryType.CREDIT) {
                    balance = balance.add(entry.getAmount()).setScale(2, RoundingMode.HALF_UP);
                } else {
                    balance = balance.subtract(entry.getAmount()).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }

        return balance.setScale(2, RoundingMode.HALF_UP);
    }
}
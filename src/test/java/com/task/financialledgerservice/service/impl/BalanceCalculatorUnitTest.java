package com.task.financialledgerservice.service.impl;

import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.TransactionEntry;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.model.enums.EntryType;
import com.task.financialledgerservice.repository.TransactionEntryRepository;
import com.task.financialledgerservice.validation.BalanceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceCalculatorUnitTest {

    @Mock
    private TransactionEntryRepository transactionEntryRepository;

    @InjectMocks
    private BalanceCalculator balanceCalculator;

    private Account assetAccount;
    private Account liabilityAccount;

    @BeforeEach
    void setUp() {
        assetAccount = new Account("Asset Acc", AccountType.ASSET);
        assetAccount.setId(1L);

        liabilityAccount = new Account("Liability Acc", AccountType.LIABILITY);
        liabilityAccount.setId(2L);
    }

    @Test
    void calculateBalance_AssetAccount_DebitIncreasesCreditDecreases() {
        List<TransactionEntry> entries = List.of(
                createTransactionEntry(1L, EntryType.DEBIT, new BigDecimal("100.00")),
                createTransactionEntry(1L, EntryType.CREDIT, new BigDecimal("50.00")),
                createTransactionEntry(1L, EntryType.DEBIT, new BigDecimal("25.00"))
        );
        when(transactionEntryRepository.findByAccountId(1L)).thenReturn(entries);

        BigDecimal balance = balanceCalculator.calculateBalance(assetAccount);

        // ASSET: +100 (DEBIT) - 50 (CREDIT) + 25 (DEBIT) = 75
        assertThat(balance).isEqualByComparingTo(new BigDecimal("75.00"));
    }

    @Test
    void calculateBalance_LiabilityAccount_CreditIncreasesDebitDecreases() {

        List<TransactionEntry> entries = List.of(
                createTransactionEntry(2L, EntryType.CREDIT, new BigDecimal("200.00")),
                createTransactionEntry(2L, EntryType.DEBIT, new BigDecimal("75.00")),
                createTransactionEntry(2L, EntryType.CREDIT, new BigDecimal("50.00"))
        );
        when(transactionEntryRepository.findByAccountId(2L)).thenReturn(entries);

        BigDecimal balance = balanceCalculator.calculateBalance(liabilityAccount);

        // LIABILITY: +200 (CREDIT) - 75 (DEBIT) + 50 (CREDIT) = 275
        // LIABILITY: +200 (CREDIT) - 75 (DEBIT) + 50 (CREDIT) = 175
        assertThat(balance).isEqualByComparingTo(new BigDecimal("175.00"));
    }

    @Test
    void calculateBalance_EmptyEntries_ReturnsZero() {
        when(transactionEntryRepository.findByAccountId(1L)).thenReturn(List.of());

        BigDecimal balance = balanceCalculator.calculateBalance(assetAccount);

        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private TransactionEntry createTransactionEntry(Long accountId, EntryType type, BigDecimal amount) {
        TransactionEntry entry = new TransactionEntry();
        Account dummyAccount = new Account();
        dummyAccount.setId(accountId);
        entry.setAccount(dummyAccount);
        entry.setType(type);
        entry.setAmount(amount);
        return entry;
    }
}
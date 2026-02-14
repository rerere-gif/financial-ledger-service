package com.task.financialledgerservice.repository;

import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.Transaction;
import com.task.financialledgerservice.model.TransactionEntry;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.model.enums.EntryType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionEntryRepository entryRepository;

    @Test
    void findAllByEntriesAccountId_ShouldReturnTransactions_WhenAccountHasEntries() {
        Account account = new Account("Test Acc for Transactions", AccountType.ASSET);
        entityManager.persist(account);
        entityManager.flush();

        Transaction transaction = new Transaction("Test Trans", OffsetDateTime.now());
        entityManager.persist(transaction);
        entityManager.flush();

        TransactionEntry entry = new TransactionEntry(transaction, account, EntryType.DEBIT, new BigDecimal("100.00"));
        transaction.addEntry(entry);
        entityManager.persist(entry);
        entityManager.flush();

        List<TransactionEntry> entries = entryRepository.findByAccountId(account.getId());

        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).getTransaction().getId()).isEqualTo(transaction.getId());
        assertThat(entries.get(0).getAccount().getId()).isEqualTo(account.getId());
    }
}
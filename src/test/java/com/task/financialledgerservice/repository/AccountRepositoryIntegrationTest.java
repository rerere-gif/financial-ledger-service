package com.task.financialledgerservice.repository;

import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.enums.AccountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findByAccountName_ShouldReturnAccount_WhenFound() {
        Account account = new Account("Integration Test Account", AccountType.ASSET);
        entityManager.persistAndFlush(account);

        Optional<Account> found = accountRepository.findByName("Integration Test Account");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Integration Test Account");
    }

    @Test
    void findByName_ShouldReturnEmptyOptional_WhenNotFound() {
        Optional<Account> found = accountRepository.findByName("Non Existent Account");

        assertThat(found).isEmpty();
    }
}
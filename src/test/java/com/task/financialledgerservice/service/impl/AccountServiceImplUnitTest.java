package com.task.financialledgerservice.service.impl;

import com.task.financialledgerservice.dto.AccountDto;
import com.task.financialledgerservice.exception.AccountAlreadyExistsException;
import com.task.financialledgerservice.mapper.AccountMapper;
import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        account = new Account("Test Asset Account", AccountType.ASSET);
        account.setId(1L);

        accountDto = new AccountDto();
        accountDto.setId(1L);
        accountDto.setName("Test Asset Account");
        accountDto.setType(AccountType.ASSET);
        accountDto.setBalance(BigDecimal.ZERO);
    }

    @Test
    void createAccount_ShouldSaveNewAccount_WhenNameIsUnique() {
        when(accountRepository.findByName("Test Asset Account")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.createAccount("Test Asset Account", AccountType.ASSET);

        assertThat(result.getName()).isEqualTo("Test Asset Account");
        assertThat(result.getType()).isEqualTo(AccountType.ASSET);
        verify(accountRepository, times(1)).findByName("Test Asset Account");
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void createAccount_ShouldThrowException_WhenNameAlreadyExists() {
        when(accountRepository.findByName("Existing Account")).thenReturn(Optional.of(new Account()));

        assertThatThrownBy(() -> accountService.createAccount("Existing Account", AccountType.LIABILITY))
                .isInstanceOf(AccountAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(accountRepository, times(1)).findByName("Existing Account");
        verify(accountRepository, never()).save(any(Account.class));
        verify(accountMapper, never()).toDto(any(Account.class));
    }
}
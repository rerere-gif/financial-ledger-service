package com.task.financialledgerservice.service.impl;

import com.task.financialledgerservice.dto.AccountDto;
import com.task.financialledgerservice.mapper.AccountMapper;
import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.repository.AccountRepository;
import com.task.financialledgerservice.service.AccountService;
import com.task.financialledgerservice.exception.AccountAlreadyExistsException;
import com.task.financialledgerservice.exception.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @Transactional
    public AccountDto createAccount(String name, AccountType type) {
        Optional<Account> existingAccount = accountRepository.findByName(name);
        if (existingAccount.isPresent()) {
            throw new AccountAlreadyExistsException("Account with name '" + name + "' already exists.");
        }
        Account account = new Account(name, type);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " not found."));
        return accountMapper.toDto(account);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(accountMapper::toDto)
                .toList();
    }
}
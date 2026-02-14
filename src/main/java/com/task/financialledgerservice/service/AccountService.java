package com.task.financialledgerservice.service;

import com.task.financialledgerservice.dto.AccountDto;
import com.task.financialledgerservice.model.enums.AccountType;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(String name, AccountType type);
    List<AccountDto> getAllAccounts();
    AccountDto getAccountById(Long id);
}
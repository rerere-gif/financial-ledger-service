package com.task.financialledgerservice.mapper;

import com.task.financialledgerservice.dto.AccountDto;
import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.validation.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final BalanceCalculator balanceCalculator;

    public AccountDto toDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setType(account.getType());
        dto.setBalance(balanceCalculator.calculateBalance(account));
        return dto;
    }
}
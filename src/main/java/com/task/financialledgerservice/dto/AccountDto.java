package com.task.financialledgerservice.dto;

import com.task.financialledgerservice.model.enums.AccountType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
}
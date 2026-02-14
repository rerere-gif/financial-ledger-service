package com.task.financialledgerservice.dto;

import com.task.financialledgerservice.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequestDto {

    @NotBlank(message = "Account name cannot be blank")
    @Size(max = 255, message = "Account name must be less than 255 characters")
    private String name;

    @NotNull(message = "Account type cannot be null")
    private AccountType type;
}
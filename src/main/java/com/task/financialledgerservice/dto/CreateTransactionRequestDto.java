package com.task.financialledgerservice.dto;

import com.task.financialledgerservice.model.enums.EntryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CreateTransactionRequestDto {

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Entries list cannot be null")
    @Valid
    private List<Entry> entries;

    @Getter
    @Setter
    @NoArgsConstructor
    @Data
    public static class Entry {
        @NotNull(message = "Account ID cannot be null")
        private Long accountId;

        @NotNull(message = "Entry type cannot be null")
        private EntryType type;

        @Positive(message = "Amount must be positive")
        @NotNull(message = "Amount cannot be null")
        private BigDecimal amount;
    }
}
package com.task.financialledgerservice.service.impl;

import com.task.financialledgerservice.dto.CreateTransactionRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.mapper.TransactionMapper;
import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.Transaction;
import com.task.financialledgerservice.model.TransactionEntry;
import com.task.financialledgerservice.repository.AccountRepository;
import com.task.financialledgerservice.repository.TransactionEntryRepository;
import com.task.financialledgerservice.repository.TransactionRepository;
import com.task.financialledgerservice.service.TransactionService;
import com.task.financialledgerservice.exception.AccountNotFoundException;
import com.task.financialledgerservice.exception.TransactionNotFoundException;
import com.task.financialledgerservice.validation.TransactionValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEntryRepository entryRepository;
    private final AccountRepository accountRepository;
    private final TransactionValidator transactionValidator;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionDto createTransaction(@Valid CreateTransactionRequestDto requestDto) {
        // 1. Валидации (теперь через компонент)
        transactionValidator.validateCreateTransactionRequest(requestDto);

        // 2. Создание сущности Transaction
        Transaction transaction = new Transaction(requestDto.getDescription(), OffsetDateTime.now());

        // 3. Обработка записей (entries)
        List<TransactionEntry> entries = new ArrayList<>();
        for (CreateTransactionRequestDto.Entry entryDto : requestDto.getEntries()) {
            // 3.1. Найти счёт по ID
            Account account = accountRepository.findById(entryDto.getAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Account with id " + entryDto.getAccountId() + " not found."));

            // 3.2. Создать сущность TransactionEntry
            TransactionEntry entry = new TransactionEntry(transaction, account, entryDto.getType(), entryDto.getAmount());
            entries.add(entry);
        }

        // 4. Установить связи и сохранить
        for (TransactionEntry entry : entries) {
            transaction.addEntry(entry); // Поддерживаем связь в сущности Transaction
        }

        Transaction savedTransaction = transactionRepository.save(transaction); // Сохраняем транзакцию и каскадно - её записи

        // 5. Преобразование в DTO (теперь через mapper)
        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + id + " not found.")); // <-- Новое исключение
        return transactionMapper.toDto(transaction); // <-- Маппим через mapper
    }

    @Override
    public List<TransactionDto> getTransactionsByAccountId(Long accountId) {
        // Найдём все TransactionEntry для данного счёта
        List<TransactionEntry> entries = entryRepository.findByAccountId(accountId);

        // Извлечём ID транзакций
        List<Long> transactionIds = entries.stream()
                .map(entry -> entry.getTransaction().getId())
                .distinct()
                .toList();

        // Найдём сами транзакции по этим ID
        List<Transaction> transactions = transactionRepository.findAllById(transactionIds);

        // Преобразуем в DTO (теперь через mapper)
        return transactions.stream()
                .map(transactionMapper::toDto) // <-- Используем метод mapper
                .collect(Collectors.toList());
    }
}
package com.task.financialledgerservice.service.impl;

import com.task.financialledgerservice.dto.CreateTransactionRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.exception.AccountNotFoundException;
import com.task.financialledgerservice.exception.InsufficientEntriesException;
import com.task.financialledgerservice.exception.UnbalancedTransactionException;
import com.task.financialledgerservice.mapper.TransactionMapper;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.model.Account;
import com.task.financialledgerservice.model.Transaction;
import com.task.financialledgerservice.model.enums.EntryType;
import com.task.financialledgerservice.repository.AccountRepository;
import com.task.financialledgerservice.repository.TransactionEntryRepository;
import com.task.financialledgerservice.repository.TransactionRepository;
import com.task.financialledgerservice.validation.TransactionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplUnitTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionValidator transactionValidator;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private CreateTransactionRequestDto requestDto;

    @BeforeEach
    void setUp() {
        CreateTransactionRequestDto.Entry entry1 = new CreateTransactionRequestDto.Entry();
        entry1.setAccountId(1L);
        entry1.setType(EntryType.DEBIT);
        entry1.setAmount(new BigDecimal("100.00"));

        CreateTransactionRequestDto.Entry entry2 = new CreateTransactionRequestDto.Entry();
        entry2.setAccountId(2L);
        entry2.setType(EntryType.CREDIT);
        entry2.setAmount(new BigDecimal("100.00"));

        requestDto = new CreateTransactionRequestDto();
        requestDto.setDescription("Test Transaction");
        requestDto.setEntries(Arrays.asList(entry1, entry2));
    }

    @Test
    void createTransaction_ShouldSaveTransaction_WhenValidAndBalanced() {
        Account acc1 = new Account("Acc 1", AccountType.ASSET);
        acc1.setId(1L);
        Account acc2 = new Account("Acc 2", AccountType.LIABILITY);
        acc2.setId(2L);


        doNothing().when(transactionValidator).validateCreateTransactionRequest(requestDto);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(acc1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(acc2));

        Transaction savedTransaction = new Transaction("Test Transaction", OffsetDateTime.now());
        savedTransaction.setId(1L);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionDto expectedDto = new TransactionDto();
        expectedDto.setId(1L);
        expectedDto.setDescription("Test Transaction");
        when(transactionMapper.toDto(savedTransaction)).thenReturn(expectedDto);

        TransactionDto result = transactionService.createTransaction(requestDto);

        assertThat(result.getDescription()).isEqualTo("Test Transaction");
        verify(transactionValidator, times(1)).validateCreateTransactionRequest(requestDto);
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).findById(2L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(transactionMapper, times(1)).toDto(savedTransaction);
    }

    @Test
    void createTransaction_ShouldThrowException_WhenAccountDoesNotExist() {
        CreateTransactionRequestDto.Entry validEntry1 = new CreateTransactionRequestDto.Entry();
        validEntry1.setAccountId(1L);
        validEntry1.setType(EntryType.DEBIT);
        validEntry1.setAmount(new BigDecimal("100.00"));

        CreateTransactionRequestDto.Entry invalidEntry = new CreateTransactionRequestDto.Entry();
        invalidEntry.setAccountId(999L);
        invalidEntry.setType(EntryType.CREDIT);
        invalidEntry.setAmount(new BigDecimal("100.00"));

        CreateTransactionRequestDto requestDtoInvalidAccount = new CreateTransactionRequestDto();
        requestDtoInvalidAccount.setDescription("Test Transaction");
        requestDtoInvalidAccount.setEntries(Arrays.asList(validEntry1, invalidEntry));

        doNothing().when(transactionValidator).validateCreateTransactionRequest(requestDtoInvalidAccount);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account("Acc 1", AccountType.ASSET)));
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(requestDtoInvalidAccount))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("not found");

        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(accountRepository, times(1)).findById(999L);
        verify(transactionValidator, times(1)).validateCreateTransactionRequest(requestDtoInvalidAccount);
        verify(transactionMapper, never()).toDto(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenTransactionIsNotBalanced() {
        CreateTransactionRequestDto unbalancedRequest = new CreateTransactionRequestDto();
        unbalancedRequest.setDescription("Unbalanced Transaction");

        CreateTransactionRequestDto.Entry entry1 = new CreateTransactionRequestDto.Entry();
        entry1.setAccountId(1L);
        entry1.setType(EntryType.DEBIT);
        entry1.setAmount(new BigDecimal("100.00"));

        CreateTransactionRequestDto.Entry entry2 = new CreateTransactionRequestDto.Entry();
        entry2.setAccountId(2L);
        entry2.setType(EntryType.CREDIT);
        entry2.setAmount(new BigDecimal("50.00"));

        unbalancedRequest.setEntries(Arrays.asList(entry1, entry2));

        doThrow(new UnbalancedTransactionException("Transaction is not balanced.", new ArrayList<>()))
                .when(transactionValidator).validateCreateTransactionRequest(unbalancedRequest);

        assertThatThrownBy(() -> transactionService.createTransaction(unbalancedRequest))
                .isInstanceOf(UnbalancedTransactionException.class)
                .hasMessageContaining("not balanced");

        verify(transactionValidator, times(1)).validateCreateTransactionRequest(unbalancedRequest);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionMapper, never()).toDto(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenLessThanTwoEntries() {
        CreateTransactionRequestDto invalidRequest = new CreateTransactionRequestDto();
        invalidRequest.setDescription("Too Few Entries");
        CreateTransactionRequestDto.Entry singleEntry = new CreateTransactionRequestDto.Entry();
        singleEntry.setAccountId(1L);
        singleEntry.setType(EntryType.DEBIT);
        singleEntry.setAmount(new BigDecimal("100.00"));
        invalidRequest.setEntries(List.of(singleEntry));

        doThrow(new InsufficientEntriesException("A transaction must have at least two entries."))
                .when(transactionValidator).validateCreateTransactionRequest(invalidRequest);

        assertThatThrownBy(() -> transactionService.createTransaction(invalidRequest))
                .isInstanceOf(InsufficientEntriesException.class)
                .hasMessageContaining("at least two entries");

        verify(transactionValidator, times(1)).validateCreateTransactionRequest(invalidRequest);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(transactionMapper, never()).toDto(any(Transaction.class));
    }
}
package com.task.financialledgerservice.controller;

import com.task.financialledgerservice.dto.CreateTransactionRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.dto.TransactionEntryDto;
import com.task.financialledgerservice.model.enums.EntryType;
import com.task.financialledgerservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void createTransaction_ShouldReturnCreatedTransactionAndStatus201() throws Exception {
        CreateTransactionRequestDto requestDto = new CreateTransactionRequestDto();
        requestDto.setDescription("Test successful transaction");

        CreateTransactionRequestDto.Entry debitEntry = new CreateTransactionRequestDto.Entry();
        debitEntry.setAccountId(1L);
        debitEntry.setType(EntryType.DEBIT);
        debitEntry.setAmount(new BigDecimal("100.00"));

        CreateTransactionRequestDto.Entry creditEntry = new CreateTransactionRequestDto.Entry();
        creditEntry.setAccountId(2L);
        creditEntry.setType(EntryType.CREDIT);
        creditEntry.setAmount(new BigDecimal("100.00"));

        requestDto.setEntries(List.of(debitEntry, creditEntry));

        TransactionDto responseDto = new TransactionDto();
        responseDto.setId(1L);
        responseDto.setDescription("Test successful transaction");
        responseDto.setTransactionDate(OffsetDateTime.now());
        TransactionEntryDto responseEntry1 = new TransactionEntryDto();
        responseEntry1.setId(1L);
        responseEntry1.setAccountId(1L);
        responseEntry1.setAccountName("Asset Account");
        responseEntry1.setType(EntryType.DEBIT);
        responseEntry1.setAmount(new BigDecimal("100.00"));
        TransactionEntryDto responseEntry2 = new TransactionEntryDto();
        responseEntry2.setId(2L);
        responseEntry2.setAccountId(2L);
        responseEntry2.setAccountName("Liability Account");
        responseEntry2.setType(EntryType.CREDIT);
        responseEntry2.setAmount(new BigDecimal("100.00"));
        responseDto.setEntries(List.of(responseEntry1, responseEntry2));

        given(transactionService.createTransaction(any(CreateTransactionRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()) // Проверяем статус 201
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test successful transaction"))
                .andExpect(jsonPath("$.entries", hasSize(2)));
    }

    @Test
    void createTransaction_ShouldReturn400_WhenValidationFails() throws Exception {
        CreateTransactionRequestDto requestDto = new CreateTransactionRequestDto();
        requestDto.setDescription("Test invalid transaction");

        CreateTransactionRequestDto.Entry invalidEntry = new CreateTransactionRequestDto.Entry();
        invalidEntry.setAccountId(1L);
        invalidEntry.setType(EntryType.DEBIT);
        invalidEntry.setAmount(new BigDecimal("-100.00"));

        CreateTransactionRequestDto.Entry balancingEntry = new CreateTransactionRequestDto.Entry();
        balancingEntry.setAccountId(2L);
        balancingEntry.setType(EntryType.CREDIT);
        balancingEntry.setAmount(new BigDecimal("100.00"));

        requestDto.setEntries(List.of(invalidEntry, balancingEntry));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("amount")))
                .andExpect(content().string(containsString("positive")));
    }

    @Test
    void getTransactionById_ShouldReturnTransactionAndStatus200() throws Exception {
        Long transactionId = 1L;
        TransactionDto responseDto = new TransactionDto();
        responseDto.setId(transactionId);
        responseDto.setDescription("Retrieved transaction");
        responseDto.setTransactionDate(OffsetDateTime.now());

        given(transactionService.getTransactionById(transactionId)).willReturn(responseDto);

        mockMvc.perform(get("/api/transactions/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Retrieved transaction"));
    }

    @Test
    void getTransactionById_ShouldReturn404_WhenNotFound() throws Exception {
        Long nonExistentId = 999999L;

        willThrow(new com.task.financialledgerservice.exception.TransactionNotFoundException("Transaction with id " + nonExistentId + " not found."))
                .given(transactionService).getTransactionById(nonExistentId);

        mockMvc.perform(get("/api/transactions/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }

    @Test
    void createTransaction_ShouldReturn400_WhenBusinessLogicFails() throws Exception {
        CreateTransactionRequestDto requestDto = new CreateTransactionRequestDto();
        requestDto.setDescription("Test business logic failure");
        CreateTransactionRequestDto.Entry entry = new CreateTransactionRequestDto.Entry();
        entry.setAccountId(999L); // Не существует
        entry.setType(EntryType.DEBIT);
        entry.setAmount(new BigDecimal("100.00"));
        requestDto.setEntries(List.of(entry));

        willThrow(new com.task.financialledgerservice.exception.AccountNotFoundException("Account with id 999 not found."))
                .given(transactionService).createTransaction(any(CreateTransactionRequestDto.class));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("not found")));
    }
}
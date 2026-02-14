package com.task.financialledgerservice.controller;

import com.task.financialledgerservice.dto.AccountDto;
import com.task.financialledgerservice.dto.CreateAccountRequestDto;
import com.task.financialledgerservice.dto.TransactionDto;
import com.task.financialledgerservice.model.enums.AccountType;
import com.task.financialledgerservice.service.AccountService;
import com.task.financialledgerservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_ShouldReturnCreatedAccountAndStatus201() throws Exception {
        // Given
        CreateAccountRequestDto requestDto = new CreateAccountRequestDto();
        requestDto.setName("Mocked New Account");
        requestDto.setType(AccountType.ASSET);

        AccountDto responseDto = new AccountDto();
        responseDto.setId(1L);
        responseDto.setName("Mocked New Account");
        responseDto.setType(AccountType.ASSET);
        responseDto.setBalance(BigDecimal.ZERO);

        given(accountService.createAccount(any(String.class), any(AccountType.class)))
                .willReturn(responseDto);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Mocked New Account"))
                .andExpect(jsonPath("$.type").value("ASSET"));
    }

    @Test
    void getTransactionsByAccountId_ShouldReturnListOfTransactionsAndStatus200() throws Exception {
        Long accountId = 1L;

        TransactionDto transactionDto1 = new TransactionDto();
        transactionDto1.setId(1L);
        transactionDto1.setDescription("Test Transaction 1");

        TransactionDto transactionDto2 = new TransactionDto();
        transactionDto2.setId(2L);
        transactionDto2.setDescription("Test Transaction 2");

        List<TransactionDto> transactionList = Arrays.asList(transactionDto1, transactionDto2);

        given(transactionService.getTransactionsByAccountId(eq(accountId)))
                .willReturn(transactionList);

        mockMvc.perform(get("/api/accounts/" + accountId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}
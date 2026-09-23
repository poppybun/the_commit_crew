package com.thecommitcrew.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.thecommitcrew.domain.enums.AccountStatus;
import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.exception.AccountNotFoundException;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.model.Order;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private static final Long TEST_ACCOUNT_ID = 1L;
    private static final String TEST_ACCOUNT_HOLDER = "John Doe";
    private static final String TEST_CURRENCY = "USD";

    private Account testAccount;
    private Money testBalance;

    @BeforeEach
    public void setUp() {
        testBalance = new Money(new BigDecimal("10000.00"), TEST_CURRENCY);
        testAccount = new Account(
        TEST_ACCOUNT_ID,
        TEST_ACCOUNT_HOLDER,
        testBalance,
        AccountStatus.ACTIVE,
        1L,
        LocalDateTime.now(),
        new com.thecommitcrew.domain.validator.DefaultAccountStatusValidator()
    );
    }

    @Test
    public void testGetAccount_Success() throws Exception {
        when(accountService.getAccount(TEST_ACCOUNT_ID)).thenReturn(testAccount);

        mockMvc.perform(get("/accounts/{id}", TEST_ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(TEST_ACCOUNT_ID))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(accountService, times(1)).getAccount(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetAccount_NotFound() throws Exception {
        when(accountService.getAccount(TEST_ACCOUNT_ID))
            .thenThrow(new AccountNotFoundException("Account not found: " + TEST_ACCOUNT_ID));

        mockMvc.perform(get("/accounts/{id}", TEST_ACCOUNT_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));

        verify(accountService, times(1)).getAccount(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetAccountBalance_Success() throws Exception {
        when(accountService.getBalance(TEST_ACCOUNT_ID)).thenReturn(testBalance);

        mockMvc.perform(get("/accounts/{id}/balance", TEST_ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountId").value(TEST_ACCOUNT_ID))
            .andExpect(jsonPath("$.cashBalance.amount").value("10000.0"));

        verify(accountService, times(1)).getBalance(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetAccountBalance_NotFound() throws Exception {
        when(accountService.getBalance(TEST_ACCOUNT_ID))
            .thenThrow(new AccountNotFoundException("Account not found: " + TEST_ACCOUNT_ID));

        mockMvc.perform(get("/accounts/{id}/balance", TEST_ACCOUNT_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    public void testGetAccountPositions_Success() throws Exception {
        Position position = new Position(TEST_ACCOUNT_ID, "AAPL", 100L, new BigDecimal("150.00"));
        List<Position> positions = List.of(position);

        when(accountService.getPositions(TEST_ACCOUNT_ID)).thenReturn(positions);

        mockMvc.perform(get("/accounts/{id}/positions", TEST_ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].symbol").value("AAPL"))
            .andExpect(jsonPath("$[0].quantity").value(100))
            .andExpect(jsonPath("$[0].averageCost").value("150.0"));

        verify(accountService, times(1)).getPositions(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetAccountPositions_NotFound() throws Exception {
        when(accountService.getPositions(TEST_ACCOUNT_ID))
            .thenThrow(new AccountNotFoundException("Account not found: " + TEST_ACCOUNT_ID));

        mockMvc.perform(get("/accounts/{id}/positions", TEST_ACCOUNT_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    public void testGetAccountOrders_Success() throws Exception {
        Order order = new Order(
            UUID.randomUUID(),
            TEST_ACCOUNT_ID,
            "AAPL",
            OrderSide.BUY,
            100L,
            new BigDecimal("150.0"),
            OrderStatus.FILLED,
            LocalDateTime.now(),
            "idempotency-key-123"
        );
        List<Order> orders = List.of(order);

        when(accountService.getOrders(TEST_ACCOUNT_ID)).thenReturn(orders);

        mockMvc.perform(get("/accounts/{id}/orders", TEST_ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].symbol").value("AAPL"))
            .andExpect(jsonPath("$[0].quantity").value(100))
            .andExpect(jsonPath("$[0].status").value("FILLED"));

        verify(accountService, times(1)).getOrders(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetAccountOrders_NotFound() throws Exception {
        when(accountService.getOrders(TEST_ACCOUNT_ID))
            .thenThrow(new AccountNotFoundException("Account not found: " + TEST_ACCOUNT_ID));

        mockMvc.perform(get("/accounts/{id}/orders", TEST_ACCOUNT_ID))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"));
    }

    @Test
    public void testGetAccountPositions_Empty() throws Exception {
        when(accountService.getPositions(TEST_ACCOUNT_ID)).thenReturn(List.of());

        mockMvc.perform(get("/accounts/{id}/positions", TEST_ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.empty()));
    }

    @Test
    public void testGetAccountOrders_Empty() throws Exception {
        when(accountService.getOrders(TEST_ACCOUNT_ID)).thenReturn(List.of());

        mockMvc.perform(get("/accounts/{id}/orders", TEST_ACCOUNT_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.empty()));
    }
}
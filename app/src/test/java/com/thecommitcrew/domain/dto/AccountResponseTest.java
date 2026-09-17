package com.thecommitcrew.domain.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thecommitcrew.domain.enums.AccountStatus;

public class AccountResponseTest {

    private AccountResponse response;

    @BeforeEach
    void setUp() {
        response = new AccountResponse();
    }

    @Test
    @DisplayName("Test response creation")
    void testAccountResponseCreation() {
        Long accountId = 1L;
        String holderName = "Pippo";
        BigDecimal cashBalance = new BigDecimal("10000.00");
        AccountStatus status = AccountStatus.ACTIVE;
        LocalDateTime lastUpdated = LocalDateTime.now();
        
        response.setAccountId(accountId);
        response.setHolderName(holderName);
        response.setCashBalance(cashBalance);
        response.setStatus(status);
        response.setLastUpdated(lastUpdated);
        
        assertNotNull(response);
    }
    
    @Test
    @DisplayName("Test all methods")
    void testAccountResponseGettersSetters() {
        AccountResponse response = new AccountResponse();
        
        Long accountId = 1L;
        response.setAccountId(accountId);
        assertEquals(accountId, response.getAccountId());
        
        String holderName = "Paperino";
        response.setHolderName(holderName);
        assertEquals(holderName, response.getHolderName());
        
        BigDecimal cashBalance = new BigDecimal("25000.50");
        response.setCashBalance(cashBalance);
        assertEquals(cashBalance, response.getCashBalance());
        
        AccountStatus status = AccountStatus.ACTIVE;
        response.setStatus(status);
        assertEquals(status, response.getStatus());
        
        LocalDateTime lastUpdated = LocalDateTime.now();
        response.setLastUpdated(lastUpdated);
        assertEquals(lastUpdated, response.getLastUpdated());
    }

}

package com.thecommitcrew.domain.model;

import com.thecommitcrew.domain.enums.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    
    private Account account;
    
    @BeforeEach
    public void setUp() {
        account = new Account(
            1L,
            "John Doe",
            new BigDecimal("10000.00"),
            AccountStatus.ACTIVE,
            1L,
            LocalDateTime.now()
        );
    }
    
    // Test 1: Successful debit
    @Test
    public void testDebit_Success() {
        BigDecimal initialBalance = account.getCashBalance();
        BigDecimal debitAmount = new BigDecimal("1000.00");
        
        account.debit(debitAmount);
        
        assertEquals(initialBalance.subtract(debitAmount), account.getCashBalance());
    }
    
    // Test 2: Debit with insufficient funds
    @Test
    public void testDebit_InsufficientFunds() {
        BigDecimal debitAmount = new BigDecimal("20000.00");
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.debit(debitAmount);
        });
    }
    
    // Test 3: Successful credit
    @Test
    public void testCredit_Success() {
        BigDecimal initialBalance = account.getCashBalance();
        BigDecimal creditAmount = new BigDecimal("5000.00");
        
        account.credit(creditAmount);
        
        assertEquals(initialBalance.add(creditAmount), account.getCashBalance());
    }
    
    // Additional test: Debit from inactive account
    @Test
    public void testDebit_InactiveAccount() {
        account.setStatus(AccountStatus.SUSPENDED);
        
        assertThrows(IllegalStateException.class, () -> {
            account.debit(new BigDecimal("100.00"));
        });
    }
    
    // Additional test: Debit with negative amount
    @Test
    public void testDebit_NegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.debit(new BigDecimal("-1000.00"));
        });
    }
    
    // Additional test: Credit with negative amount
    @Test
    public void testCredit_NegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.credit(new BigDecimal("-1000.00"));
        });
    }
    
    // Additional test: isActive check
    @Test
    public void testIsActive() {
        assertTrue(account.isActive());
        
        account.setStatus(AccountStatus.SUSPENDED);
        assertFalse(account.isActive());
        
        account.setStatus(AccountStatus.CLOSED);
        assertFalse(account.isActive());
    }
}
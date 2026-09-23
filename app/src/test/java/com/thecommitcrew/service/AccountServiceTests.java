package com.thecommitcrew.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thecommitcrew.domain.enums.AccountStatus;
import com.thecommitcrew.domain.exception.AccountNotFoundException;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.domain.validator.AccountStatusValidator;
import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.persistence.repository.OrderRepository;
import com.thecommitcrew.persistence.repository.PositionRepository;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    private static final Long TEST_ACCOUNT_ID = 1L;
    private static final Long INVALID_ACCOUNT_ID = 999L;
    private static final BigDecimal TEST_PRICE = new BigDecimal("100.00");

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AccountStatusValidator statusValidator;

    private AccountService accountService;
    private Account testAccount;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(accountRepository, positionRepository, orderRepository);
        testAccount = new Account(
            TEST_ACCOUNT_ID,
            "John Doe",
            new Money(new BigDecimal("10000.00"), "USD"),
            AccountStatus.ACTIVE,
            1L,
            LocalDateTime.now(),
            statusValidator
        );
    }

    @Test
    void getAccount_WithValidId_ReturnsAccount() {
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccount(TEST_ACCOUNT_ID);

        assertEquals(testAccount, result);
        verify(accountRepository).findById(TEST_ACCOUNT_ID);
    }

    @Test
    void getAccount_WithInvalidId_ThrowsException() {
        when(accountRepository.findById(INVALID_ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> 
            accountService.getAccount(INVALID_ACCOUNT_ID)
        );
        verify(accountRepository).findById(INVALID_ACCOUNT_ID);
    }

    @Test
    void getPositions_WithValidAccount_ReturnsList() {
        List<Position> positions = Arrays.asList(
            createPosition("AAPL", 10L),
            createPosition("GOOGL", 5L)
        );
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(positionRepository.findByAccountId(TEST_ACCOUNT_ID)).thenReturn(positions);

        List<Position> result = accountService.getPositions(TEST_ACCOUNT_ID);

        assertThat(result).hasSize(2);
        verify(positionRepository).findByAccountId(TEST_ACCOUNT_ID);
    }

    @Test
    void getPositions_WithInvalidAccount_ThrowsException() {
        when(accountRepository.findById(INVALID_ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> 
            accountService.getPositions(INVALID_ACCOUNT_ID)
        );
    }

    @Test
    void getBalance_ReturnsAccountBalance() {
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        Money result = accountService.getBalance(TEST_ACCOUNT_ID);

        assertEquals(testAccount.getCashBalance(), result);
        verify(accountRepository).findById(TEST_ACCOUNT_ID);
    }

    private Position createPosition(String symbol, Long quantity) {
        return new Position(TEST_ACCOUNT_ID, symbol, quantity, TEST_PRICE);
    }
}
package com.thecommitcrew.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thecommitcrew.domain.exception.AccountNotActiveException;
import com.thecommitcrew.domain.exception.AccountNotFoundException;
import com.thecommitcrew.domain.exception.DuplicateOrderException;
import com.thecommitcrew.domain.exception.InsufficientFundsException;
import com.thecommitcrew.domain.exception.InsufficientHoldingsException;
import com.thecommitcrew.domain.exception.InstrumentNotFoundException;
import com.thecommitcrew.domain.exception.NegativePriceException;

@RestController
public class TestController {
    @GetMapping("/test-account-not-found")
    void throwAccountNotFound() {
        throw new AccountNotFoundException("Account with ID 123 not found");
    }

    @GetMapping("/test-instrument-not-found")
    void throwInstrumentNotFound() {
        throw new InstrumentNotFoundException("Instrument AAPL not found");
    }

    @GetMapping("/test-account-not-active")
    void throwAccountNotActive() {
        throw new AccountNotActiveException("Account is not active");
    }

    @GetMapping("/test-insufficient-funds")
    void throwInsufficientFunds() {
        throw new InsufficientFundsException("Insufficient funds to complete transaction");
    }

    @GetMapping("/test-insufficient-holdings")
    void throwInsufficientHoldings() {
        throw new InsufficientHoldingsException("Insufficient holdings to sell");
    }

    @GetMapping("/test-duplicate-order")
    void throwDuplicateOrder() {
        throw new DuplicateOrderException("Order with ID 456 already exists");
    }

    @GetMapping("/test-illegal-argument")
    void throwIllegalArgument() {
        throw new IllegalArgumentException("Invalid argument provided");
    }

    @GetMapping("/test-negative-price")
    void throwNegativePrice() {
        throw new NegativePriceException("Price cannot be negative");
    }

    @GetMapping("/test-generic-exception")
    void throwGenericException() {
        throw new RuntimeException("Unexpected error");
    }
}
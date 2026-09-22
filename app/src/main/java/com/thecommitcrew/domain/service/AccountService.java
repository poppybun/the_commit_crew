package com.thecommitcrew.domain.service;

import java.util.*;
import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.domain.model.Order;
import org.springframework.stereotype.Service;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.exception.AccountNotFoundException;

@Service 
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

public Account getAccount(Long accountId) {
    return accountRepository.findById(accountId)
        .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
}

    public List<Position> getPositions(Long accountId) {
        getAccount(accountId); // Validate account exists for now
        return null; // Will replace with actual account retrieval logic
    }

    public List<Order> getOrders(Long accountId) {
        getAccount(accountId); // Validate account exists for now
        return null; // Will replace with actual account retrieval logic
    }

    public Money getBalance(Long accountId) {
        return getAccount(accountId).getCashBalance();
    }
    
}
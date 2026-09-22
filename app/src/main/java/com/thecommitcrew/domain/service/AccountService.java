package com.thecommitcrew.domain.service;

import java.util.*;
import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.domain.model.Order;
import org.springframework.stereotype.Service;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.exception.AccountNotFoundException;
import com.thecommitcrew.persistence.repository.PositionRepository;
import com.thecommitcrew.persistence.repository.OrderRepository;

@Service 
public class AccountService {

    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final OrderRepository orderRepository;

    public AccountService(AccountRepository accountRepository, PositionRepository positionRepository, OrderRepository orderRepository) {
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
        this.orderRepository = orderRepository;
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
    }

    public List<Position> getPositions(Long accountId) {
        getAccount(accountId); // Validate account exists
        return positionRepository.findByAccountId(accountId);
    }

    public List<Order> getOrders(Long accountId) {
        getAccount(accountId); // Validate account exists
        return orderRepository.findByAccountId(accountId);
    }

    public Money getBalance(Long accountId) {
        return getAccount(accountId).getCashBalance();
    }
    
}
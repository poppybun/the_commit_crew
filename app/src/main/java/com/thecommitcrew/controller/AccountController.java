package com.thecommitcrew.controller;

import org.springframework.web.bind.annotation.RestController;

import com.thecommitcrew.domain.dto.AccountResponseDTO;
import com.thecommitcrew.domain.dto.BalanceResponseDTO;
import com.thecommitcrew.domain.dto.OrderResponseDTO;
import com.thecommitcrew.domain.dto.PositionResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController 
@RequestMapping("/accounts") 
public class AccountController {
    
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable("id") Long accountId) { 

    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BalanceResponseDTO> getAccountBalance(@PathVariable("id") Long accountId) { 

    }

    @GetMapping("/{id}/positions")
    public ResponseEntity<List<PositionResponseDTO>> getAccountPositions(@PathVariable("id") Long accountId) { 

    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getAccountOrders(@PathVariable("id") Long accountId) { 
        
    }
}

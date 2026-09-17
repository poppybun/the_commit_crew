package com.thecommitcrew.domain.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;

public class OrderResponseTest {

    private OrderResponse response;

    @BeforeEach
    void setUp() {
        response = new OrderResponse();
    }

    @Test
    @DisplayName("Test response creation")
    void testOrderResponseSetters() {
        UUID id = UUID.randomUUID();
        String accountId = "ACC123";
        String symbol = "AAPL";
        OrderSide side = OrderSide.BUY;
        int quantity = 100;
        BigDecimal price = new BigDecimal("150.00");
        OrderStatus status = OrderStatus.FILLED;
        LocalDateTime createdOn = LocalDateTime.now();
        String idempotencyKey = "IDEMPOTENCY_123";

        response.setId(id);
        response.setAccountId(accountId);
        response.setSymbol(symbol);
        response.setSide(side);
        response.setQuantity(quantity);
        response.setPrice(price);
        response.setStatus(status);
        response.setCreatedOn(createdOn);
        response.setIdempotencyKey(idempotencyKey);
        
        assertNotNull(response);
    }
    
    @Test
    @DisplayName("Test all methods")
    void testOrderResponseGettersSetters() {
        UUID id = UUID.randomUUID();
        response.setId(id);
        assertEquals(id, response.getId());
        
        String accountId = "ACC123";
        response.setAccountId(accountId);
        assertEquals(accountId, response.getAccountId());
        
        String symbol = "AAPL";
        response.setSymbol(symbol);
        assertEquals(symbol, response.getSymbol());
        
        OrderSide side = OrderSide.SELL;
        response.setSide(side);
        assertEquals(side, response.getSide());
        
        int quantity = 50;
        response.setQuantity(quantity);
        assertEquals(quantity, response.getQuantity());
        
        BigDecimal price = new BigDecimal("155.50");
        response.setPrice(price);
        assertEquals(price, response.getPrice());
        
        OrderStatus status = OrderStatus.NEW;
        response.setStatus(status);
        assertEquals(status, response.getStatus());
        
        LocalDateTime createdOn = LocalDateTime.now();
        response.setCreatedOn(createdOn);
        assertEquals(createdOn, response.getCreatedOn());
        
        String idempotencyKey = "KEY_123";
        response.setIdempotencyKey(idempotencyKey);
        assertEquals(idempotencyKey, response.getIdempotencyKey());
    }

}

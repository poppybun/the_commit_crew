package com.thecommitcrew.domain.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PosistionResponseTest {

    private PositionResponse response;

    @BeforeEach
    void setUp() {
        response = new PositionResponse();
    }

    @Test
    @DisplayName("Test response creation")
    void testPositionResponseCreation() {
        String symbol = "AAPL";
        int quantity = 100;
        BigDecimal averagePrice = new BigDecimal("150.00");
        
        response.setSymbol(symbol);
        response.setQuantity(quantity);
        response.setAverageCost(averagePrice);
        
        assertNotNull(response);
    }
    
    @Test
    @DisplayName("Test all methods")
    void testPositionResponseGettersSetters() {
        String symbol = "MSFT";
        response.setSymbol(symbol);
        assertEquals(symbol, response.getSymbol());
        
        int quantity = 50;
        response.setQuantity(quantity);
        assertEquals(quantity, response.getQuantity());
        
        BigDecimal averagePrice = new BigDecimal("300.00");
        response.setAverageCost(averagePrice);
        assertEquals(averagePrice, response.getAverageCost());
    }

}

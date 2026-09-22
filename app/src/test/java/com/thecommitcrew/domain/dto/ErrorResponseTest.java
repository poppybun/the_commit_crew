package com.thecommitcrew.domain.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

    private ErrorResponse response;

    @BeforeEach
    void setUp() {
        response = new ErrorResponse();
    }

    @Test
    @DisplayName("Test response creation")
    void testErrorResponseCreation() {
        String code = "ORDER_REJECTED";
        String message = "Insufficient funds in account";
        LocalDateTime timestamp = LocalDateTime.now();
        
        response.setCode(code);
        response.setMessage(message);
        response.setTimestamp(timestamp);
        
        assertNotNull(response);
    }
    
    @Test
    @DisplayName("Test all methods")
    void testErrorResponseGettersSetters() {
        String code = "INSUFFICIENT_BALANCE";
        response.setCode(code);
        assertEquals(code, response.getCode());
        
        String message = "Account does not have enough cash to complete this order";
        response.setMessage(message);
        assertEquals(message, response.getMessage());
        
        LocalDateTime timestamp = LocalDateTime.now();
        response.setTimestamp(timestamp);
        assertEquals(timestamp, response.getTimestamp());
    }

}

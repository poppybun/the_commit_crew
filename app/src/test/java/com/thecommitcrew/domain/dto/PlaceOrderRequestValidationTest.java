package com.thecommitcrew.domain.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.thecommitcrew.domain.enums.OrderSide;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

/*
* Using Validator instance since this class has annotations
* Validator checks that the validation rules work correctly
*/
public class PlaceOrderRequestValidationTest {
    
    private Validator validator;
    private PlaceOrderRequest request;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        request = new PlaceOrderRequest();
    }
    
    @Test
    @DisplayName("Test valid PlaceOrderRequest")
    void testValidRequest() {
        request.setSymbol("AAPL");
        request.setSide(OrderSide.BUY);
        request.setQuantity(100);
        request.setPrice(new BigDecimal("150.00"));
        
        Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Nested
    @DisplayName("Invalid quantity tests")
    class testInvalidQuantity {

        @Test
        @DisplayName("Test zero quantity")
        void testQuantityCannotBeZero() {
            request.setSymbol("AAPL");
            request.setSide(OrderSide.BUY);
            request.setQuantity(0);
            request.setPrice(new BigDecimal("150.00"));
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }
        
        @Test
        @DisplayName("Test negative quantity")
        void testQuantityMustBePositive() {
            request.setSymbol("AAPL");
            request.setSide(OrderSide.BUY);
            request.setQuantity(-5);
            request.setPrice(new BigDecimal("150.00"));
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test null fields")
    class testNullFields {

        @Test
        @DisplayName("Test null price")
        void testPriceCannotBeNull() {
            request.setSymbol("AAPL");
            request.setSide(OrderSide.BUY);
            request.setQuantity(100);
            request.setPrice(null);
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }
        
        @Test
        @DisplayName("Test negative price")
        void testPriceMustBePositive() {
            request.setSymbol("AAPL");
            request.setSide(OrderSide.BUY);
            request.setQuantity(100);
            request.setPrice(new BigDecimal("-10.00"));
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Test null symbol")
        void testSymbolCannotBeNull() {
            request.setSymbol(null);
            request.setSide(OrderSide.BUY);
            request.setQuantity(100);
            request.setPrice(new BigDecimal("150.00"));
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Test empty symbol")
        void testSymbolCannotBeBlank() {
            request.setSymbol("   ");
            request.setSide(OrderSide.BUY);
            request.setQuantity(100);
            request.setPrice(new BigDecimal("150.00"));
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Test null side")
        void testSideCannotBeNull() {
            request.setSymbol("AAPL");
            request.setSide(null);
            request.setQuantity(100);
            request.setPrice(new BigDecimal("150.00"));
            
            Set<ConstraintViolation<PlaceOrderRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
        }
    }
    
}

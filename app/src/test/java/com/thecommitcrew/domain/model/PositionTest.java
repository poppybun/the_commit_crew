package com.thecommitcrew.domain.model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thecommitcrew.domain.enums.AccountStatus;
import com.thecommitcrew.domain.exception.NegativePriceException;

public class PositionTest {

    private Position position;
    private BigDecimal quantity;
    private BigDecimal zeroQuantity;
    private BigDecimal negativeQuantity;
    private BigDecimal averageCost;
    private BigDecimal currentPrice;
    private BigDecimal negativeCurrentPrice;
    private BigDecimal loweredCurrentPrice;
    private Account account;

    @BeforeEach
    void setUp() { 
        quantity = new BigDecimal("10");
        zeroQuantity = new BigDecimal("0");
        negativeQuantity = new BigDecimal("-5");
        averageCost = new BigDecimal("100");
        currentPrice = new BigDecimal("150");
        negativeCurrentPrice = new BigDecimal("-10");
        loweredCurrentPrice = new BigDecimal("50");
        account = new Account(1L, "Test Account", new BigDecimal("10000"), AccountStatus.ACTIVE, 1L, LocalDateTime.now());
        position = new Position(account, "TSLA", quantity, averageCost);
    }

    @Test
    void marketValueCalculatesCorrectly() throws NegativePriceException{

        assertEquals(new BigDecimal("1500"), position.marketValue(currentPrice));
    }

    @Test
    void marketValueWithZeroQuantity() throws NegativePriceException {
        Position position1 = new Position(account, "TSLA", zeroQuantity, averageCost);
        
        assertEquals(new BigDecimal("0"), position1.marketValue(currentPrice));
    }

    // Short position case
    @Test
    void marketValueWithNegativeQuantity() throws NegativePriceException {
        Position position1 = new Position(account, "TSLA", negativeQuantity, averageCost);
        
        assertEquals(new BigDecimal("-750"), position1.marketValue(currentPrice));
    }

    @Test
    void marketValueWithNegativePrice() {
        assertThrows(NegativePriceException.class, () -> {
            position.marketValue(negativeCurrentPrice);
        });
    }

    @Test
    void unrealizedProfitCalculatesCorrectly() throws NegativePriceException {

        assertEquals(new BigDecimal("500"), position.unrealizedProfitLoss(currentPrice));
    }

    @Test
    void unrealizedLossCalculatesCorrectly() throws NegativePriceException {

        assertEquals(new BigDecimal("-500"), position.unrealizedProfitLoss(loweredCurrentPrice));
    }

    @Test
    void unrealizedProfitLossWithNegativePrice() {
        assertThrows(NegativePriceException.class, () -> {
            position.unrealizedProfitLoss(negativeCurrentPrice);
        });
    }

    //TODO: apply() tests & edge cases
}
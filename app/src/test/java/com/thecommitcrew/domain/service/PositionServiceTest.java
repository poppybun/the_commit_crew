package com.thecommitcrew.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.exception.NegativePriceException;
import com.thecommitcrew.domain.model.Order;
import com.thecommitcrew.domain.model.Position;

public class PositionServiceTest {

    private PositionService positionService;
    private Position position;
    private Order buyOrder;
    private Order sellOrder;
    private BigDecimal quantity;
    private BigDecimal zeroQuantity;
    private BigDecimal averageCost;
    private BigDecimal currentPrice;
    private BigDecimal negativeCurrentPrice;
    private BigDecimal loweredCurrentPrice;

    @BeforeEach
    void setUp() {
        positionService = new PositionService();
        quantity = new BigDecimal("10");
        zeroQuantity = new BigDecimal("0");
        averageCost = new BigDecimal("100");
        currentPrice = new BigDecimal("150");
        negativeCurrentPrice = new BigDecimal("-10");
        loweredCurrentPrice = new BigDecimal("50");
        position = new Position(1L, "TSLA", quantity, averageCost);
        buyOrder = new Order(UUID.randomUUID(), 1L, "TSLA", OrderSide.BUY, new BigDecimal("5"), new BigDecimal("110"), OrderStatus.NEW, LocalDateTime.now(), "idempotency-1");
        sellOrder = new Order(UUID.randomUUID(), 1L, "TSLA", OrderSide.SELL, new BigDecimal("3"), new BigDecimal("120"), OrderStatus.NEW, LocalDateTime.now(), "idempotency-3");
    }

    @Test
    void marketValueCalculatesCorrectly() throws NegativePriceException {
        assertEquals(new BigDecimal("1500"), positionService.marketValue(position, currentPrice));
    }

    @Test
    void marketValueWithZeroQuantity() throws NegativePriceException {
        Position positionZero = new Position(1L, "TSLA", zeroQuantity, averageCost);
        assertEquals(new BigDecimal("0"), positionService.marketValue(positionZero, currentPrice));
    }

    @Test
    void marketValueWithNegativePrice() {
        assertThrows(NegativePriceException.class, () -> {
            positionService.marketValue(position, negativeCurrentPrice);
        });
    }

    @Test
    void unrealizedProfitCalculatesCorrectly() throws NegativePriceException {
        assertEquals(new BigDecimal("500"), positionService.unrealizedProfitLoss(position, currentPrice));
    }

    @Test
    void unrealizedLossCalculatesCorrectly() throws NegativePriceException {
        assertEquals(new BigDecimal("-500"), positionService.unrealizedProfitLoss(position, loweredCurrentPrice));
    }

    @Test
    void unrealizedProfitLossWithNegativePrice() {
        assertThrows(NegativePriceException.class, () -> {
            positionService.unrealizedProfitLoss(position, negativeCurrentPrice);
        });
    }

    @Test
    void applyBuyOrderIncreasesQuantity() {
        Position updatedPosition = positionService.applyOrder(position, buyOrder);
        
        assertEquals(new BigDecimal("15"), updatedPosition.getQuantity());
    }

    @Test
    void applyBuyOrderUpdatesAverageCost() {
        Position updatedPosition = positionService.applyOrder(position, buyOrder);
        
        assertEquals(new BigDecimal("103"), updatedPosition.getAverageCost());
    }

    @Test
    void applySellOrderDecreasesQuantity() {
        Position updatedPosition = positionService.applyOrder(position, sellOrder);
        
        assertEquals(new BigDecimal("7"), updatedPosition.getQuantity());
    }

    @Test
    void applySellOrderKeepsAverageCost() {
        Position updatedPosition = positionService.applyOrder(position, sellOrder);
        
        assertEquals(new BigDecimal("100"), updatedPosition.getAverageCost());
    }

    @Test
    void applySellOrderClosingPosition() {
        Order sellOrderClosing = new Order(UUID.randomUUID(), 1L, "TSLA", OrderSide.SELL, new BigDecimal("10"), new BigDecimal("120"), OrderStatus.NEW, LocalDateTime.now(), "idempotency-1");
        Position updatedPosition = positionService.applyOrder(position, sellOrderClosing);
        
        assertEquals(BigDecimal.ZERO, updatedPosition.getQuantity());
        assertEquals(BigDecimal.ZERO, updatedPosition.getAverageCost());
    }

    @Test
    void applyOrderThrowsExceptionWhenSellingMoreThanOwned() {
        Order sellOrderTooMany = new Order(UUID.randomUUID(), 1L, "TSLA", OrderSide.SELL, new BigDecimal("15"), new BigDecimal("120"), OrderStatus.NEW, LocalDateTime.now(), "idempotency-1");
        
        assertThrows(IllegalArgumentException.class, () -> {
            positionService.applyOrder(position, sellOrderTooMany);
        });
    }
}

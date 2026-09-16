package com.thecommitcrew.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;

public class Order {
    private final UUID id;
    private final long accountId;
    private final String symbol;
    private final OrderSide side;
    private final int quantity;
    private final BigDecimal price;
    private OrderStatus status;
    private final LocalDateTime createdOn;
    private final String idempotencyKey;

    public Order(UUID id, long accountId, String symbol, OrderSide side, int quantity, BigDecimal price, OrderStatus status, LocalDateTime createdOn, String idempotencyKey) {
        this.id = id;
        this.accountId = accountId;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.createdOn = createdOn;
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

}
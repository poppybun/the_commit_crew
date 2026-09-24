package com.thecommitcrew.service;

import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderService {

    public Order placeOrder(
        Account account,
        String symbol,
        OrderSide side,
        long quantity,
        BigDecimal price,
        String idempotencyKey,
        long availableHoldings
    ) {
        OrderStatus status = determineStatus(account, side, quantity, availableHoldings);

        return new Order(
            UUID.randomUUID(),
            account.getAccountId(),
            symbol,
            side,
            quantity,
            price,
            status,
            LocalDateTime.now(),
            idempotencyKey
        );
    }

    private OrderStatus determineStatus(Account account, OrderSide side, long quantity, long availableHoldings) {
        if (!account.isActive()) {
            return OrderStatus.REJECTED;
        }
        if (side == OrderSide.SELL && quantity > availableHoldings) {
            return OrderStatus.REJECTED;
        }
        return OrderStatus.NEW;
    }

    // placeOrder(PlaceOrderRequest), cancelOrder(orderId), validateOrder(), executeOrder()
}
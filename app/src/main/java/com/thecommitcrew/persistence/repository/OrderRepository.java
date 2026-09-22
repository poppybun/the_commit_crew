package com.thecommitcrew.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.model.Order;

@Repository
public interface OrderRepository {
    Optional<Order> findById(UUID orderId);
    Order save(Order order);
    List<Order> findByAccountId(Long accountId);
    List<Order> findByAccountIdAndStatus(Long accountId, OrderStatus status);
}

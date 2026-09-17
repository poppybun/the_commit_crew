package com.thecommitcrew.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.domain.model.Order;
import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.exception.NegativePriceException;

public class PositionService {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    
    public Position applyOrder(Position position, Order order) {
        BigDecimal orderQuantity = order.getQuantity();
        BigDecimal currentQuantity = position.getQuantity();
        BigDecimal newQuantity = calculateNewQuantity(currentQuantity, orderQuantity, order.getSide());
        
        if (newQuantity.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("Cannot sell more shares than owned");
        }
        
        BigDecimal newAverageCost = calculateUpdatedCostBasis(position, order, newQuantity);
        
        return new Position(position.getAccountId(), order.getSymbol(), newQuantity, newAverageCost);
    }
    
    private BigDecimal calculateNewQuantity(BigDecimal currentQuantity, BigDecimal orderQuantity, OrderSide side) {
        return side.apply(currentQuantity, orderQuantity);
    }
    
    private BigDecimal calculateUpdatedCostBasis(Position position, Order order, BigDecimal newQuantity) {
        
        if (newQuantity.compareTo(ZERO) == 0) {
            return ZERO;
        }
        
        if (order.getSide() == OrderSide.SELL) {
            return position.getAverageCost();
        }

        BigDecimal costBasis = calculateCostBasis(position);
        BigDecimal tradeTotal = order.getQuantity().multiply(order.getPrice());
        BigDecimal newTotal = costBasis.add(tradeTotal);
        BigDecimal averageCost = newTotal.divide(newQuantity, RoundingMode.HALF_UP);

        return averageCost;
    }

    public BigDecimal marketValue(Position position, BigDecimal currentPrice) throws NegativePriceException {
        if (currentPrice.compareTo(ZERO) < 0) {
            throw new NegativePriceException("Current price of asset cannot be negative.");
        }
        return position.getQuantity().multiply(currentPrice);
    }

    public BigDecimal unrealizedProfitLoss(Position position, BigDecimal currentPrice) throws NegativePriceException {
        BigDecimal costBasis = calculateCostBasis(position);
        BigDecimal profitLoss = marketValue(position, currentPrice).subtract(costBasis);
        return profitLoss;
    }

    public BigDecimal calculateCostBasis(Position position) {
        return position.getQuantity().multiply(position.getAverageCost());
    }
}
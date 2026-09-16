/**
 * Represents a financial position - a holding of a security within an account.
 */

package com.thecommitcrew.domain.model;
import java.math.BigDecimal;
import com.thecommitcrew.domain.exception.NegativePriceException;

public class Position {
    private final Long accountId;
    private final String symbol;
    private final BigDecimal quantity;
    private final BigDecimal averageCost;

    public Position(Account account, String symbol, BigDecimal quantity, BigDecimal averageCost) {
        this.accountId = account.getAccountId();
        this.symbol = symbol;
        this.quantity = quantity;
        this.averageCost = averageCost;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public BigDecimal marketValue(BigDecimal currentPrice) throws NegativePriceException {
        if (currentPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativePriceException("Current price of asset cannot be negative.");
        }
        else {
            return quantity.multiply(currentPrice);
        }
    }

    public BigDecimal unrealizedProfitLoss(BigDecimal currentPrice) throws NegativePriceException {
        BigDecimal costBasis = quantity.multiply(averageCost);
        BigDecimal unrealizedProfitLoss = marketValue(currentPrice).subtract(costBasis);
        return unrealizedProfitLoss;
    }

    //TODO: apply() once Order exists
    /*public Position apply(Order order) {
        BigDecimal newQuantity = quantity.add(
            order.getSide() == Side.BUY 
                ? order.getQuantity() 
                : order.getQuantity().negate()
        );
        
        BigDecimal newAverageCost = averageCost;
        if (order.getSide() == Side.BUY) {
            BigDecimal currentTotal = quantity.multiply(averageCost);
            BigDecimal tradeTotal = order.getQuantity().multiply(order.getPrice());
            newAverageCost = currentTotal.add(tradeTotal)
                .divide(newQuantity, RoundingMode.HALF_UP);
        }
        
        return new Position(accountId, symbol, newQuantity, newAverageCost);
    }*/
}
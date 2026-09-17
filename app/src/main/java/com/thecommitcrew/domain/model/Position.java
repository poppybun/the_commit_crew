/**
 * Represents a financial position - a holding of a security within an account.
 */

package com.thecommitcrew.domain.model;
import java.math.BigDecimal;

public class Position {
    private final Long accountId;
    private final String symbol;
    private final long quantity;
    private final BigDecimal averageCost;

    public Position(Long accountId, String symbol, long quantity, BigDecimal averageCost) {
        this.accountId = accountId;
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

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }
}
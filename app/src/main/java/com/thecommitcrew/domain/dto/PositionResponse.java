package com.thecommitcrew.domain.dto;

import java.math.BigDecimal;

public class PositionResponse {

    private Long accountId;
    private String symbol;
    private long quantity;
    private BigDecimal averageCost;

    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public long getQuantity() {
        return quantity;
    }
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getAverageCost() {
        return averageCost;
    }
    public void setAverageCost(BigDecimal averageCost) {
        this.averageCost = averageCost;
    }

}

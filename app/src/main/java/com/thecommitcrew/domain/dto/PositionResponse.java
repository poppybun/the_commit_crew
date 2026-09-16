package com.thecommitcrew.domain.dto;

import java.math.BigDecimal;

public class PositionResponse {

    private String symbol;
    private int quantity;
    private BigDecimal averagePrice;
    private BigDecimal currentMarketPrice;
    private BigDecimal totalValue;


    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getAveragePrice() {
        return averagePrice;
    }
    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }
    public BigDecimal getCurrentMarketPrice() {
        return currentMarketPrice;
    }
    public void setCurrentMarketPrice(BigDecimal currentMarketPrice) {
        this.currentMarketPrice = currentMarketPrice;
    }
    public BigDecimal getTotalValue() {
        return totalValue;
    }
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

}

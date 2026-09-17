package com.thecommitcrew.domain.enums;
import java.math.BigDecimal;

public enum OrderSide {
    BUY,
    SELL;

    public BigDecimal apply(BigDecimal current, BigDecimal orderQty) {
        switch (this) {
            case BUY:
                return current.add(orderQty);
            case SELL:
                return current.subtract(orderQty);
            default:
                throw new IllegalArgumentException("Unknown order side: " + this);
        }
    }
}

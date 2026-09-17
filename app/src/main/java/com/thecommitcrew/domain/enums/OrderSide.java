package com.thecommitcrew.domain.enums;

public enum OrderSide {
    BUY,
    SELL;

    public long apply(long current, long orderQty) {
        switch (this) {
            case BUY:
                return current + orderQty;
            case SELL:
                return current - orderQty;
            default:
                throw new IllegalArgumentException("Unknown order side: " + this);
        }
    }
}

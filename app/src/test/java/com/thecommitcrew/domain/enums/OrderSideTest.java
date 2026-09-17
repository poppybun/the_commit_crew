package com.thecommitcrew.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class OrderSideTest {
    @Test
    void buyAppliesAddition() {
        BigDecimal result = OrderSide.BUY.apply(new BigDecimal("10"), new BigDecimal("5"));
        assertEquals(new BigDecimal("15"), result);
    }

    @Test
    void sellAppliesSubtraction() {
        BigDecimal result = OrderSide.SELL.apply(new BigDecimal("10"), new BigDecimal("3"));
        assertEquals(new BigDecimal("7"), result);
    }

    @Test
    void applyWithZeroQuantity() {
        assertEquals(new BigDecimal("5"), OrderSide.BUY.apply(BigDecimal.ZERO, new BigDecimal("5")));
        assertEquals(BigDecimal.ZERO, OrderSide.SELL.apply(new BigDecimal("5"), new BigDecimal("5")));
    }
}

package com.thecommitcrew.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.thecommitcrew.domain.dto.PlaceOrderRequest;
import com.thecommitcrew.domain.enums.OrderSide;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PlaceOrderRequestValidationTest {

	@Test
	void testValidRequest() {
		PlaceOrderRequest request = new PlaceOrderRequest(
			123L,
			"AAPL",
			OrderSide.BUY,
			100L,
			new BigDecimal("182.45"),
			"idem-001"
		);

		assertEquals(123L, request.getAccountId());
		assertEquals("AAPL", request.getSymbol());
		assertEquals(OrderSide.BUY, request.getSide());
		assertEquals(100L, request.getQuantity());
		assertEquals(new BigDecimal("182.45"), request.getPrice());
		assertEquals("idem-001", request.getIdempotencyKey());
	}

	@Test
	void testInvalidQuantity() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new PlaceOrderRequest(
				123L,
				"AAPL",
				OrderSide.BUY,
				0L,
				new BigDecimal("182.45"),
				"idem-001"
			)
		);

		assertEquals("quantity must be greater than zero", exception.getMessage());
	}

	@Test
	void testNullFields() {
		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> new PlaceOrderRequest(
				0L,
				"AAPL",
				OrderSide.BUY,
				100L,
				new BigDecimal("182.45"),
				"idem-001"
			)
		);

		assertEquals("accountId must be positive", exception.getMessage());
	}
}

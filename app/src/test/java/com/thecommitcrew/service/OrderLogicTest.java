package com.thecommitcrew.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.thecommitcrew.domain.enums.AccountStatus;
import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.model.Order;
import com.thecommitcrew.domain.validator.DefaultAccountStatusValidator;
import com.thecommitcrew.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderLogicTest {

	private static final LocalDateTime LAST_UPDATED = LocalDateTime.parse("2026-09-16T09:30:00");

	private OrderService orderService;
	private Account activeAccount;

	@BeforeEach
	void setUp() {
		orderService = new OrderService();
		activeAccount = new Account(
			123L,
			"John Doe",
			new Money(new BigDecimal("10000.00"), "USD"),
			AccountStatus.ACTIVE,
			1L,
			LAST_UPDATED,
			new DefaultAccountStatusValidator()  // Use concrete implementation
		);
	}

	@Test
	void testBuy_Success() {
		Order order = orderService.placeOrder(
			activeAccount,
			"AAPL",
			OrderSide.BUY,
			100L,
			new BigDecimal("182.45"),
			"idem-001",
			0L
		);

		assertNotNull(order.getId());
		assertEquals(123L, order.getAccountId());
		assertEquals("AAPL", order.getSymbol());
		assertEquals(OrderSide.BUY, order.getSide());
		assertEquals(100L, order.getQuantity());
		assertEquals(new BigDecimal("182.45"), order.getPrice());
		assertEquals(OrderStatus.NEW, order.getStatus());
		assertEquals("idem-001", order.getIdempotencyKey());
	}

	@Test
	void testSell_InsufficientHoldings() {
		Order order = orderService.placeOrder(
			activeAccount,
			"AAPL",
			OrderSide.SELL,
			100L,
			new BigDecimal("182.45"),
			"idem-002",
			50L
		);

		assertEquals(OrderSide.SELL, order.getSide());
		assertEquals(OrderStatus.REJECTED, order.getStatus());
	}

	@Test
	void testReject_InactiveAccount() {
		activeAccount = activeAccount.updateStatus(AccountStatus.SUSPENDED);

		Order order = orderService.placeOrder(
			activeAccount,
			"AAPL",
			OrderSide.BUY,
			10L,
			new BigDecimal("182.45"),
			"idem-003",
			0L
		);

		assertEquals(OrderSide.BUY, order.getSide());
		assertEquals(OrderStatus.REJECTED, order.getStatus());
	}

	@Test
	void testOrderConstructorRetainsValues() {
		UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		LocalDateTime createdOn = LocalDateTime.parse("2026-09-16T09:30:00");

		Order order = new Order(
			orderId,
			123L,
			"AAPL",
			OrderSide.BUY,
			100L,
			new BigDecimal("182.45"),
			OrderStatus.NEW,
			createdOn,
			"idem-001"
		);

		assertNotNull(order.getId());
		assertEquals(orderId, order.getId());
		assertEquals(123L, order.getAccountId());
		assertEquals("AAPL", order.getSymbol());
		assertEquals(OrderSide.BUY, order.getSide());
		assertEquals(100L, order.getQuantity());
		assertEquals(new BigDecimal("182.45"), order.getPrice());
		assertEquals(OrderStatus.NEW, order.getStatus());
		assertEquals(createdOn, order.getCreatedOn());
		assertEquals("idem-001", order.getIdempotencyKey());
	}

	@Test
	void testOrderStatusCanChange() {
		Order order = new Order(
			UUID.randomUUID(),
			123L,
			"AAPL",
			OrderSide.BUY,
			100L,
			new BigDecimal("182.45"),
			OrderStatus.NEW,
			LocalDateTime.parse("2026-09-16T09:30:00"),
			"idem-004"
		);

		order.setStatus(OrderStatus.FILLED);

		assertEquals(OrderStatus.FILLED, order.getStatus());
	}

	@Test
	void testOrderSellMetadataIsRetained() {
		UUID orderId = UUID.fromString("22222222-2222-2222-2222-222222222222");
		LocalDateTime createdOn = LocalDateTime.parse("2026-09-16T10:00:00");

		Order order = new Order(
			orderId,
			456L,
			"MSFT",
			OrderSide.SELL,
			50L,
			new BigDecimal("199.99"),
			OrderStatus.REJECTED,
			createdOn,
			"idem-002"
		);

		assertEquals(orderId, order.getId());
		assertEquals(456L, order.getAccountId());
		assertEquals("MSFT", order.getSymbol());
		assertEquals(OrderSide.SELL, order.getSide());
		assertEquals(50L, order.getQuantity());
		assertEquals(new BigDecimal("199.99"), order.getPrice());
		assertEquals(OrderStatus.REJECTED, order.getStatus());
		assertEquals(createdOn, order.getCreatedOn());
		assertEquals("idem-002", order.getIdempotencyKey());
	}
}

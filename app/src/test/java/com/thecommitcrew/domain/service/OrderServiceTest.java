package com.thecommitcrew.domain.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thecommitcrew.domain.dto.PlaceOrderRequestDTO;
import com.thecommitcrew.domain.enums.AccountStatus;
import com.thecommitcrew.domain.enums.AssetClass;
import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.exception.AccountNotFoundException;
import com.thecommitcrew.domain.exception.DuplicateOrderException;
import com.thecommitcrew.domain.exception.InstrumentNotFoundException;
import com.thecommitcrew.domain.exception.InsufficientFundsException;
import com.thecommitcrew.domain.exception.InsufficientHoldingsException;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Instrument;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.model.Order;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.domain.validator.BasicInstrumentSymbolValidator;
import com.thecommitcrew.domain.validator.DefaultAccountStatusValidator;
import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.persistence.repository.InstrumentRepository;
import com.thecommitcrew.persistence.repository.OrderRepository;
import com.thecommitcrew.persistence.repository.PositionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Long ACCOUNT_ID = 1L;
    private static final String SYMBOL = "AAPL";
    private static final String IDEMPOTENCY_KEY = "idem-123";

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private InstrumentRepository instrumentRepository;

    private OrderService orderService;
    private Account activeAccount;
    private Instrument tradableInstrument;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, accountRepository, positionRepository, instrumentRepository);
        activeAccount = new Account(
            ACCOUNT_ID,
            "Jane Doe",
            new Money(new BigDecimal("10000.00"), "USD"),
            AccountStatus.ACTIVE,
            1L,
            LocalDateTime.now(),
            new DefaultAccountStatusValidator()
        );
        tradableInstrument = new Instrument(
            "instr-1",
            SYMBOL,
            "Apple Inc.",
            AssetClass.EQUITY,
            Currency.getInstance("USD"),
            true,
            new BasicInstrumentSymbolValidator()
        );
    }

    @Test
    void placeOrder_buyWithFunds_fillsOrder() {
        PlaceOrderRequestDTO request = request(ACCOUNT_ID, SYMBOL, OrderSide.BUY, 10L, "100.00", IDEMPOTENCY_KEY);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(activeAccount));
        when(instrumentRepository.findBySymbol(SYMBOL)).thenReturn(Optional.of(tradableInstrument));
        when(positionRepository.findByAccountIdAndSymbol(ACCOUNT_ID, SYMBOL)).thenReturn(Optional.empty());
        when(orderRepository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.placeOrder(request);

        assertEquals(OrderStatus.FILLED, order.getStatus());
        assertEquals(ACCOUNT_ID, order.getAccountId());
        assertEquals(SYMBOL, order.getSymbol());
        verify(orderRepository, times(2)).save(order);
        verify(accountRepository).save(any(Account.class));
        verify(positionRepository).save(any(Position.class));
    }

    @Test
    void placeOrder_buyWithoutFunds_rejectsOrder() {
        PlaceOrderRequestDTO request = request(ACCOUNT_ID, SYMBOL, OrderSide.BUY, 100L, "1000.00", IDEMPOTENCY_KEY);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(activeAccount));
        when(instrumentRepository.findBySymbol(SYMBOL)).thenReturn(Optional.of(tradableInstrument));
        when(positionRepository.findByAccountIdAndSymbol(ACCOUNT_ID, SYMBOL)).thenReturn(Optional.empty());
        when(orderRepository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.placeOrder(request);

        assertEquals(OrderStatus.REJECTED, order.getStatus());
        verify(accountRepository, never()).save(any(Account.class));
        verify(positionRepository, never()).save(any(Position.class));
    }

    @Test
    void placeOrder_missingAccount_throwsException() {
        PlaceOrderRequestDTO request = request(ACCOUNT_ID, SYMBOL, OrderSide.BUY, 10L, "100.00", IDEMPOTENCY_KEY);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> orderService.placeOrder(request));
    }

    @Test
    void cancelOrder_newOrder_marksCancelled() {
        UUID orderId = UUID.randomUUID();
        Order order = existingOrder(orderId, OrderSide.BUY, OrderStatus.NEW, 10L, "100.00", IDEMPOTENCY_KEY);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_nonNewOrder_throwsException() {
        UUID orderId = UUID.randomUUID();
        Order order = existingOrder(orderId, OrderSide.BUY, OrderStatus.FILLED, 10L, "100.00", IDEMPOTENCY_KEY);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId));
    }

    @Test
    void validateOrder_validRequest_doesNotThrow() {
        PlaceOrderRequestDTO request = request(ACCOUNT_ID, SYMBOL, OrderSide.BUY, 10L, "100.00", IDEMPOTENCY_KEY);

        when(instrumentRepository.findBySymbol(SYMBOL)).thenReturn(Optional.of(tradableInstrument));
        when(orderRepository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of());

        assertDoesNotThrow(() -> orderService.validateOrder(request));
    }

    @Test
    void validateOrder_duplicateIdempotency_throwsException() {
        PlaceOrderRequestDTO request = request(ACCOUNT_ID, SYMBOL, OrderSide.BUY, 10L, "100.00", IDEMPOTENCY_KEY);
        Order existingOrder = existingOrder(UUID.randomUUID(), OrderSide.BUY, OrderStatus.NEW, 5L, "99.00", IDEMPOTENCY_KEY);

        when(instrumentRepository.findBySymbol(SYMBOL)).thenReturn(Optional.of(tradableInstrument));
        when(orderRepository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of(existingOrder));

        assertThrows(DuplicateOrderException.class, () -> orderService.validateOrder(request));
    }

    @Test
    void validateOrder_missingInstrument_throwsException() {
        PlaceOrderRequestDTO request = request(ACCOUNT_ID, SYMBOL, OrderSide.BUY, 10L, "100.00", IDEMPOTENCY_KEY);

        when(instrumentRepository.findBySymbol(SYMBOL)).thenReturn(Optional.empty());

        assertThrows(InstrumentNotFoundException.class, () -> orderService.validateOrder(request));
    }

    @Test
    void executeOrder_buyOrder_fillsOrderAndPersistsChanges() {
        UUID orderId = UUID.randomUUID();
        Order order = existingOrder(orderId, OrderSide.BUY, OrderStatus.NEW, 10L, "100.00", IDEMPOTENCY_KEY);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(activeAccount));
        when(positionRepository.findByAccountIdAndSymbol(ACCOUNT_ID, SYMBOL)).thenReturn(Optional.empty());

        orderService.executeOrder(orderId);

        assertEquals(OrderStatus.FILLED, order.getStatus());
        verify(accountRepository).save(any(Account.class));
        verify(positionRepository).save(any(Position.class));
        verify(orderRepository).save(order);
    }

    @Test
    void executeOrder_sellWithoutHoldings_throwsException() {
        UUID orderId = UUID.randomUUID();
        Order order = existingOrder(orderId, OrderSide.SELL, OrderStatus.NEW, 10L, "100.00", IDEMPOTENCY_KEY);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(activeAccount));
        when(positionRepository.findByAccountIdAndSymbol(ACCOUNT_ID, SYMBOL)).thenReturn(Optional.empty());

        assertThrows(InsufficientHoldingsException.class, () -> orderService.executeOrder(orderId));
    }

    @Test
    void executeOrder_buyWithoutFunds_throwsException() {
        UUID orderId = UUID.randomUUID();
        Order order = existingOrder(orderId, OrderSide.BUY, OrderStatus.NEW, 200L, "100.00", IDEMPOTENCY_KEY);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(activeAccount));
        when(positionRepository.findByAccountIdAndSymbol(ACCOUNT_ID, SYMBOL)).thenReturn(Optional.empty());

        assertThrows(InsufficientFundsException.class, () -> orderService.executeOrder(orderId));
    }

    private PlaceOrderRequestDTO request(Long accountId, String symbol, OrderSide side, long quantity,
                                         String price, String idempotencyKey) {
        return new PlaceOrderRequestDTO(
            accountId,
            symbol,
            side,
            quantity,
            new BigDecimal(price),
            idempotencyKey
        );
    }

    private Order existingOrder(UUID orderId, OrderSide side, OrderStatus status, long quantity,
                                String price, String idempotencyKey) {
        return new Order(
            orderId,
            ACCOUNT_ID,
            SYMBOL,
            side,
            quantity,
            new BigDecimal(price),
            status,
            LocalDateTime.now(),
            idempotencyKey
        );
    }
}
package com.thecommitcrew.domain.service;

import com.thecommitcrew.domain.dto.PlaceOrderRequestDTO;
import com.thecommitcrew.domain.enums.OrderSide;
import com.thecommitcrew.domain.enums.OrderStatus;
import com.thecommitcrew.domain.exception.AccountNotActiveException;
import com.thecommitcrew.domain.exception.AccountNotFoundException;
import com.thecommitcrew.domain.exception.DuplicateOrderException;
import com.thecommitcrew.domain.exception.InstrumentNotFoundException;
import com.thecommitcrew.domain.exception.InsufficientFundsException;
import com.thecommitcrew.domain.exception.InsufficientHoldingsException;
import com.thecommitcrew.domain.exception.NegativePriceException;
import com.thecommitcrew.domain.model.Account;
import com.thecommitcrew.domain.model.Instrument;
import com.thecommitcrew.domain.model.Money;
import com.thecommitcrew.domain.model.Order;
import com.thecommitcrew.domain.model.Position;
import com.thecommitcrew.persistence.repository.AccountRepository;
import com.thecommitcrew.persistence.repository.InstrumentRepository;
import com.thecommitcrew.persistence.repository.OrderRepository;
import com.thecommitcrew.persistence.repository.PositionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private static final long ZERO_QUANTITY = 0L;

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;
    private final InstrumentRepository instrumentRepository;
    private final PositionService positionService;

    public OrderService(OrderRepository orderRepository, AccountRepository accountRepository,
                        PositionRepository positionRepository, InstrumentRepository instrumentRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
        this.instrumentRepository = instrumentRepository;
        this.positionService = new PositionService();
    }

    @Transactional
    public Order placeOrder(PlaceOrderRequestDTO request) {
        Account account = getAccount(request.accountId());
        long availableHoldings = getAvailableHoldings(request.accountId(), request.symbol());

        Order order = new Order(
            UUID.randomUUID(),
            request.accountId(),
            request.symbol(),
            request.side(),
            request.quantity(),
            request.price(),
            OrderStatus.NEW,
            LocalDateTime.now(),
            request.idempotencyKey()
        );

        validateOrder(order);

        OrderStatus status = determineStatus(
            account,
            request.side(),
            request.quantity(),
            request.price(),
            availableHoldings
        );
        order.setStatus(status);

        Order savedOrder = orderRepository.save(order);
        if (savedOrder.getStatus() == OrderStatus.NEW) {
            executeOrder(savedOrder);
        }

        return savedOrder;
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException("Only NEW orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private void validateOrder(Order order) {
        if (order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativePriceException("Price must be positive");
        }

        Instrument instrument = instrumentRepository.findBySymbol(order.getSymbol())
            .orElseThrow(() -> new InstrumentNotFoundException(
                "Instrument not found for symbol: " + order.getSymbol()
            ));

        if (!instrument.isTradable()) {
            throw new IllegalStateException("Instrument is not tradable: " + order.getSymbol());
        }

        boolean duplicateOrder = orderRepository.findByAccountId(order.getAccountId()).stream()
            .anyMatch(existingOrder -> existingOrder.getIdempotencyKey().equals(order.getIdempotencyKey()));
        if (duplicateOrder) {
            throw new DuplicateOrderException(
                "Duplicate order detected for idempotency key: " + order.getIdempotencyKey()
            );
        }
    }

    private void executeOrder(Order order) {
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException("Only NEW orders can be executed");
        }

        Account account = getAccount(order.getAccountId());
        if (!account.isActive()) {
            throw new AccountNotActiveException("Account is not active: " + order.getAccountId());
        }

        Optional<Position> existingPosition = positionRepository.findByAccountIdAndSymbol(
            order.getAccountId(),
            order.getSymbol()
        );
        long availableHoldings = existingPosition.map(Position::getQuantity).orElse(ZERO_QUANTITY);
        BigDecimal tradeValue = calculateTradeValue(order.getQuantity(), order.getPrice());

        if (order.getSide() == OrderSide.SELL && order.getQuantity() > availableHoldings) {
            throw new InsufficientHoldingsException(
                "Insufficient holdings to sell " + order.getQuantity() + " shares of " + order.getSymbol()
            );
        }

        if (order.getSide() == OrderSide.BUY
            && tradeValue.compareTo(account.getCashBalance().getAmount()) > 0) {
            throw new InsufficientFundsException(
                "Insufficient funds to buy " + order.getQuantity() + " shares of " + order.getSymbol()
            );
        }

        Money tradeAmount = new Money(tradeValue, account.getCashBalance().getCurrency());
        Account updatedAccount = order.getSide() == OrderSide.BUY
            ? account.debit(tradeAmount)
            : account.credit(tradeAmount);
        accountRepository.save(updatedAccount);

        Position basePosition = existingPosition.orElseGet(() -> new Position(
            order.getAccountId(),
            order.getSymbol(),
            ZERO_QUANTITY,
            BigDecimal.ZERO
        ));
        Position updatedPosition = positionService.applyOrder(basePosition, order);
        positionRepository.save(updatedPosition);

        order.setStatus(OrderStatus.FILLED);
        orderRepository.save(order);
    }

    private OrderStatus determineStatus(Account account, OrderSide side, long quantity, BigDecimal price,
                                        long availableHoldings) {
        if (!account.isActive()) {
            return OrderStatus.REJECTED;
        }
        if (side == OrderSide.SELL && quantity > availableHoldings) {
            return OrderStatus.REJECTED;
        }
        if (side == OrderSide.BUY
            && calculateTradeValue(quantity, price).compareTo(account.getCashBalance().getAmount()) > 0) {
            return OrderStatus.REJECTED;
        }
        return OrderStatus.NEW;
    }

    private Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    private long getAvailableHoldings(Long accountId, String symbol) {
        return positionRepository.findByAccountIdAndSymbol(accountId, symbol)
            .map(Position::getQuantity)
            .orElse(ZERO_QUANTITY);
    }

    private BigDecimal calculateTradeValue(long quantity, BigDecimal price) {
        return BigDecimal.valueOf(quantity).multiply(price);
    }
}
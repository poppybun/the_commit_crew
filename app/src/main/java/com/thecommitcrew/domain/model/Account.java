package com.thecommitcrew.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thecommitcrew.domain.enums.AccountStatus;

public class Account {
    private Long accountId;
    private String holderName;
    private BigDecimal cashBalance;
    private AccountStatus status;
    private Long version;
    private LocalDateTime lastUpdated;

    // Constructor
    public Account(Long accountId, String holderName, BigDecimal cashBalance, 
                   AccountStatus status, Long version, LocalDateTime lastUpdated) {
        this.accountId = accountId;
        this.holderName = holderName;
        this.cashBalance = cashBalance;
        this.status = status;
        this.version = version;
        this.lastUpdated = lastUpdated;
    }

    // Methods

    /**
     * Debits the specified amount from the account if it is active and has sufficient funds.
     * Throws an exception if the account is not active, the amount is non-positive, or there are insufficient funds.
     * Updates the cash balance and the last updated timestamp if the debit is successful.
     */
    public void debit(BigDecimal amount) {
        if (!isActive()) {
            throw new IllegalStateException("Cannot debit from inactive account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (amount.compareTo(this.cashBalance) > 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.cashBalance = this.cashBalance.subtract(amount);
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * Credits the specified amount to the account.
     * Throws an exception if the amount is non-positive.
     * Updates the cash balance and the last updated timestamp if the credit is successful.
     */
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        this.cashBalance = this.cashBalance.add(amount);
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public String getHolderName() {
        return holderName;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    // Setter for account status
    // Updates the account status and refreshes the last updated timestamp
    public void setStatus(AccountStatus status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
package com.thecommitcrew.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.thecommitcrew.domain.enums.AccountStatus;

public class AccountResponse {

    private Long account;
    private String holderName;
    private BigDecimal cashBalance;
    private AccountStatus status;
    private LocalDateTime lastUpdated;


    public Long getAccount() {
        return account;
    }
    public void setAccount(Long account) {
        this.account = account;
    }
    public String getHolderName() {
        return holderName;
    }
    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }
    public BigDecimal getCashBalance() {
        return cashBalance;
    }
    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}

package com.thecommitcrew.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.thecommitcrew.domain.model.Account;

@Repository
public interface AccountRepository {
    Optional<Account> findById(Long accountId);
    Account save(Account account);
}

package com.thecommitcrew.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.thecommitcrew.domain.model.Position;

@Repository
public interface PositionRepository {
    Optional<Position> findByAccountIdAndSymbol(Long accountId, String symbol);
    List<Position> findByAccountId(Long accountId);
    Position save(Position position);
}
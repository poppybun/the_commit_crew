package com.thecommitcrew.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.thecommitcrew.domain.model.Instrument;

@Repository
public interface InstrumentRepository {
    Optional<Instrument> findBySymbol(String symbol);
    Instrument save(Instrument instrument);
}
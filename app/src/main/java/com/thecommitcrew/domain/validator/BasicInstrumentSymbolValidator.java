package com.thecommitcrew.domain.validator;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.thecommitcrew.domain.exception.InstrumentNotFoundException;

/**
 * Basic Validator - checks if the symbol belongs to the valid symbols set.
 */
@Component 
public class BasicInstrumentSymbolValidator implements InstrumentSymbolValidator {

    private static final Set<String> VALID_SYMBOLS = Set.of(
        "AAPL",
        "MSFT",
        "GOOGL",
        "US10Y",
        "VFIAX",
        "CASH-USD"
    );

    @Override
    public void validateSymbol(String symbol) {
        if (!VALID_SYMBOLS.contains(symbol)) {
            throw new InstrumentNotFoundException(symbol + " is not a valid instrument symbol");
        }
    }

}

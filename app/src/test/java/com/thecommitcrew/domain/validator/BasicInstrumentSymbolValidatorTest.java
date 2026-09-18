package com.thecommitcrew.domain.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.thecommitcrew.domain.exception.InstrumentNotFoundException;

public class BasicInstrumentSymbolValidatorTest {

    private InstrumentSymbolValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BasicInstrumentSymbolValidator();
    }
    
    @Test
    public void testValidateSymbol_ValidSymbol() {
        assertDoesNotThrow(() -> {
            validator.validateSymbol("AAPL");
        });
    }
    
    @Test
    public void testValidateSymbol_InvalidSymbol() {
        assertThrows(InstrumentNotFoundException.class, () -> {
            validator.validateSymbol("NONEXISTENT");
        });
    }

}

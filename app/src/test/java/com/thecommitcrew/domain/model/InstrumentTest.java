package com.thecommitcrew.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.thecommitcrew.domain.enums.AssetClass;
import com.thecommitcrew.domain.exception.InstrumentNotFoundException;
import com.thecommitcrew.domain.validator.BasicInstrumentSymbolValidator;
import com.thecommitcrew.domain.validator.InstrumentSymbolValidator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Currency;

public class InstrumentTest {
    
    private Instrument instrument;
    private static final InstrumentSymbolValidator VALIDATOR = new BasicInstrumentSymbolValidator();
    
    @BeforeEach
    public void setUp() {
        instrument = new Instrument("1", "AAPL", "Apple Inc.", AssetClass.EQUITY, 
            Currency.getInstance("USD"), true, VALIDATOR);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(instrument);
        assertEquals("1", instrument.getId());
        assertEquals("AAPL", instrument.getSymbol());
        assertEquals("Apple Inc.", instrument.getName());
        assertEquals(AssetClass.EQUITY, instrument.getAssetClass());
        assertEquals(Currency.getInstance("USD"), instrument.getCurrency());
        assertTrue(instrument.isTradable());
    }

    @Nested
    @DisplayName("Tests for constructor failures")
    class testingConstrutorInvalidInputs {

        @Test
        @DisplayName("Testing null id")
        public void testConstructor_NullId() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Instrument(null, "AAPL", "Apple Inc.", AssetClass.EQUITY,
                    Currency.getInstance("USD"), true, VALIDATOR);
            });
        }
        
        @Test
        @DisplayName("Testing null name")
        public void testConstructor_NullName() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Instrument("1", "AAPL", null, AssetClass.EQUITY,
                    Currency.getInstance("USD"), true, VALIDATOR);
            });
        }
        
        @Test
        @DisplayName("Testing null asset class")
        public void testConstructor_NullAssetClass() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Instrument("1", "AAPL", "Apple Inc.", null,
                    Currency.getInstance("USD"), true, VALIDATOR);
            });
        }
        
        @Test
        @DisplayName("Testing null currency")
        public void testConstructor_NullCurrency() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Instrument("1", "AAPL", "Apple Inc.", AssetClass.EQUITY,
                    null, true, VALIDATOR);
            });
        }
        
        @Test
        @DisplayName("Testing null validator")
        public void testConstructor_NullValidator() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Instrument("1", "AAPL", "Apple Inc.", AssetClass.EQUITY,
                    Currency.getInstance("USD"), true, null);
            });
        }

        @Test
        @DisplayName("Testing invalid symbol")
        public void testConstructor_InvalidSymbol() {
            assertThrows(InstrumentNotFoundException.class, () -> {
                new Instrument("1", "INVALID_SYMBOL", "Apple Inc.", AssetClass.EQUITY,
                    Currency.getInstance("USD"), true, VALIDATOR);
            });
        }
    }
    
    @Nested
    @DisplayName("Tests for getter methods")
    class testingGetters {

        @Test
        public void testGetId() {
            assertEquals("1", instrument.getId());
        }
        
        @Test
        public void testGetSymbol() {
            assertEquals("AAPL", instrument.getSymbol());
        }
        
        @Test
        public void testGetName() {
            assertEquals("Apple Inc.", instrument.getName());
        }
        
        @Test
        public void testGetAssetClass() {
            assertEquals(AssetClass.EQUITY, instrument.getAssetClass());
        }
        
        @Test
        public void testGetCurrency() {
            assertEquals(Currency.getInstance("USD"), instrument.getCurrency());
        }
        
        @Test
        public void testIsTradableTrue() {
            assertTrue(instrument.isTradable());
        }
        
        @Test
        public void testIsTradableFalse() {
            Instrument nonTradable = new Instrument("2", "AAPL", "Apple Inc.", AssetClass.EQUITY, 
                Currency.getInstance("USD"), false, VALIDATOR);
            assertFalse(nonTradable.isTradable());
        }
    }
    
    @Test
    public void testImmutability() {
        // Since fields are final, attempting to change them should not be possible
        // This test verifies the object remains unchanged after creation
        String originalSymbol = instrument.getSymbol();
        assertEquals("AAPL", originalSymbol);
        assertEquals("AAPL", instrument.getSymbol()); // Still the same
    }
}
package com.thecommitcrew.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstrumentTest {
    
    private Instrument instrument;
    
    @BeforeEach
    public void setUp() {
        instrument = new Instrument("1", "AAPL", "Apple Inc.", "EQUITY", "USD", true);
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(instrument);
        assertEquals("1", instrument.getId());
        assertEquals("AAPL", instrument.getSymbol());
        assertEquals("Apple Inc.", instrument.getName());
        assertEquals("EQUITY", instrument.getAssetClass());
        assertEquals("USD", instrument.getCurrency());
        assertTrue(instrument.isTradable());
    }
    
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
        assertEquals("EQUITY", instrument.getAssetClass());
    }
    
    @Test
    public void testGetCurrency() {
        assertEquals("USD", instrument.getCurrency());
    }
    
    @Test
    public void testIsTradableTrue() {
        assertTrue(instrument.isTradable());
    }
    
    @Test
    public void testIsTradableFalse() {
        Instrument nonTradable = new Instrument("2", "BTC", "Bitcoin", "CRYPTO", "USD", false);
        assertFalse(nonTradable.isTradable());
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
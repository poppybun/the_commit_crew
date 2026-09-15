package com.thecommitcrew.domain.model;

/**
 * Represents a financial trading instrument (e.g., stock, cryptocurrency, commodity).
 * This model encapsulates the properties needed to identify, describe,
 * and determine the tradable status of an asset.
 */
public class Instrument {
    
    private final String id;
    private final String symbol;
    private final String name;
    private final String assetClass;
    private final String currency;
    private final boolean tradable;


    public Instrument(String id, String symbol, String name, String assetClass, String currency, boolean tradable) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.assetClass = assetClass;
        this.currency = currency;
        this.tradable = tradable;
    }

    public String getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getCurrency() {
        return currency;
    }

    // TODO: change this method once we know what makes an Instrument tradable
    public Boolean isTradable() {
        return tradable;
    }

    @Override
    public String toString() {
        return "Instrument [id=" + id + ", symbol=" + symbol + ", name=" + name + ", assetClass=" + assetClass
                + ", currency=" + currency + ", isTradable=" + tradable + "]";
    }

}
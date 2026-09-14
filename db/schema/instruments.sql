-- DROP TABLE IF EXISTS instruments;
CREATE TABLE IF NOT EXISTS instruments (
    symbol         VARCHAR(20) PRIMARY KEY CHECK (symbol = upper(symbol)),
    name           TEXT NOT NULL,
    asset_class    TEXT NOT NULL CHECK (asset_class IN ('Equity', 'Bond', 'Fund', 'Cash')),
    currency       TEXT NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    tradable       BOOLEAN NOT NULL DEFAULT TRUE
);
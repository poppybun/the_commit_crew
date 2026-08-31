DROP TABLE IF EXISTS instruments;
DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS orders;

CREATE TABLE instruments (
    instrument_id  SERIAL PRIMARY KEY,
    symbol         TEXT NOT NULL UNIQUE,
    name           TEXT NOT NULL,
    asset_class    TEXT NOT NULL CHECK (asset_class IN ('Equity', 'Bond', 'Fund', 'Cash')),
    currency       TEXT NOT NULL
);

-- Accounts table
CREATE TABLE accounts (
    id              BIGSERIAL PRIMARY KEY,
    account_id      VARCHAR(32) UNIQUE NOT NULL,
    holder_name     VARCHAR(255) NOT NULL,
    cash_balance    NUMERIC(18,2) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    version         INTEGER DEFAULT 0,
    last_updated    TIMESTAMP DEFAULT NOW()
);

-- Positions table
CREATE TABLE positions (
    account_id      VARCHAR(32) NOT NULL REFERENCES accounts(account_id),
    symbol          TEXT NOT NULL REFERENCES instruments(symbol),
    quantity        NUMERIC(14,4) NOT NULL,
    average_cost    NUMERIC(14,4) NOT NULL
);

CREATE TABLE orders (
    order_id     SERIAL PRIMARY KEY,
    account_id   VARCHAR(32) NOT NULL REFERENCES accounts(account_id),
    symbol       TEXT NOT NULL REFERENCES instruments(symbol),
    side         TEXT NOT NULL CHECK (side IN ('BUY', 'SELL')),
    quantity     NUMERIC(14,4) NOT NULL,
    price        NUMERIC(14,4),
    status       TEXT NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    idempotency_key TEXT UNIQUE,
    created_on    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed data ------------------------------------------------------------


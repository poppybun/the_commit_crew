DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS instruments;
DROP TABLE IF EXISTS accounts;

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
    position_id     SERIAL PRIMARY KEY,
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

INSERT INTO instruments (symbol, name, asset_class, currency) VALUES
    ('AAPL', 'Apple Inc.', 'Equity', 'USD'),
    ('MSFT', 'Microsoft Corporation', 'Equity', 'USD'),
    ('GOOGL', 'Alphabet Inc.', 'Equity', 'USD'),
    ('US10Y', 'US Treasury 10-Year Note', 'Bond', 'USD'),
    ('VFIAX', 'Vanguard 500 Index Fund', 'Fund', 'USD'),
    ('CASH-USD', 'US Dollar Cash', 'Cash', 'USD');

INSERT INTO accounts (account_id, holder_name, cash_balance, status, version) VALUES
    ('ACC-1001', 'Alice Johnson', 25000.00, 'ACTIVE', 0),
    ('ACC-1002', 'Bob Smith', 5400.50, 'ACTIVE', 0),
    ('ACC-1003', 'Carol Davis', 120000.00, 'ACTIVE', 0),
    ('ACC-1004', 'David Lee', 0.00, 'CLOSED', 0);

INSERT INTO positions (account_id, symbol, quantity, average_cost) VALUES
    ('ACC-1001', 'AAPL', 50, 150.25),
    ('ACC-1001', 'US10Y', 10, 98.50),
    ('ACC-1002', 'MSFT', 20, 310.75),
    ('ACC-1003', 'GOOGL', 15, 2750.00),
    ('ACC-1003', 'VFIAX', 100, 420.10);

INSERT INTO orders (account_id, symbol, side, quantity, price, status, idempotency_key) VALUES
    ('ACC-1001', 'AAPL', 'BUY', 50, 150.25, 'COMPLETED', 'idem-0001'),
    ('ACC-1002', 'MSFT', 'BUY', 20, 310.75, 'COMPLETED', 'idem-0002'),
    ('ACC-1003', 'GOOGL', 'BUY', 15, 2750.00, 'COMPLETED', 'idem-0003'),
    ('ACC-1001', 'AAPL', 'SELL', 10, 155.00, 'PENDING', 'idem-0004'),
    ('ACC-1002', 'MSFT', 'BUY', 5, 305.00, 'CANCELLED', 'idem-0005');

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

CREATE TABLE accounts (
    id            SERIAL PRIMARY KEY,
    account_id    SERIAL UNIQUE NOT NULL,
    client_id     INTEGER NOT NULL REFERENCES clients(client_id),
    account_type  TEXT NOT NULL CHECK (account_type IN ('ISA', 'GIA', 'SIPP')),
    opened_date   DATE NOT NULL,
    currency      TEXT NOT NULL
);

-- Positions table
CREATE TABLE positions (
    account_id     INTEGER NOT NULL REFERENCES accounts(account_id),
    symbol         TEXT NOT NULL REFERENCES instruments(symbol),
    quantity       NUMERIC(14,4) NOT NULL,
    average_cost    NUMERIC(14,4) NOT NULL
);

CREATE TABLE orders (
    order_id       SERIAL PRIMARY KEY,
    account_id     INTEGER NOT NULL REFERENCES accounts(account_id),
    instrument_id  INTEGER NOT NULL REFERENCES instruments(instrument_id),
    order_type     TEXT NOT NULL CHECK (order_type IN ('BUY', 'SELL')),
    quantity       NUMERIC(14,4) NOT NULL,
    price          NUMERIC(14,4),
    order_date     DATE NOT NULL
);

-- Seed data ------------------------------------------------------------


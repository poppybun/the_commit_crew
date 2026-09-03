DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS instruments;
DROP TABLE IF EXISTS accounts;

CREATE TABLE instruments (
    symbol         VARCHAR(20) PRIMARY KEY CHECK (symbol = upper(symbol)),
    name           TEXT NOT NULL,
    asset_class    TEXT NOT NULL CHECK (asset_class IN ('Equity', 'Bond', 'Fund', 'Cash')),
    currency       TEXT NOT NULL CHECK (currency ~ '^[A-Z]{3}$'),
    tradable       BOOLEAN NOT NULL DEFAULT TRUE
);

-- Accounts table
CREATE TABLE accounts (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id      VARCHAR(32) UNIQUE NOT NULL,
    holder_name     VARCHAR(255) NOT NULL,
    cash_balance    NUMERIC(18,2) NOT NULL CHECK (cash_balance >= 0),
    status          VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'CLOSED', 'SUSPENDED')),
    version         INT DEFAULT 0,
    last_updated    TIMESTAMP DEFAULT NOW()
);

-- Positions table
CREATE TABLE positions (
    account_id      VARCHAR(32) NOT NULL,
    symbol          VARCHAR(20) NOT NULL,
    quantity        NUMERIC(14,4) NOT NULL CHECK (quantity > 0),
    average_cost    NUMERIC(14,4) NOT NULL CHECK (average_cost > 0),
    PRIMARY KEY (account_id, symbol),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE RESTRICT,
    FOREIGN KEY (symbol) REFERENCES instruments(symbol) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE orders (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id       VARCHAR(32) NOT NULL,
    symbol           VARCHAR(20) NOT NULL,
    side             VARCHAR(4) NOT NULL CHECK (side IN ('BUY', 'SELL')),
    quantity         INT NOT NULL CHECK (quantity > 0),
    price            NUMERIC(18,2) NOT NULL CHECK (price > 0),
    status           VARCHAR(20) NOT NULL CHECK (status IN ('NEW', 'FILLED', 'REJECTED', 'CANCELLED')),
    created_on       TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE RESTRICT,
    FOREIGN KEY (symbol) REFERENCES instruments(symbol) ON DELETE RESTRICT ON UPDATE CASCADE
);


-- Indexes --
CREATE INDEX idx_positions_account_id
    ON positions(account_id);

CREATE INDEX idx_positions_symbol 
    ON positions(symbol);

CREATE INDEX idx_orders_account_id 
    ON orders(account_id);

CREATE INDEX idx_orders_symbol 
    ON orders(symbol);

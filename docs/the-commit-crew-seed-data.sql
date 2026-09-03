-- Seed data ------------------------------------------------------------

INSERT INTO instruments (symbol, name, asset_class, currency, tradable) VALUES
    ('AAPL', 'Apple Inc.', 'Equity', 'USD', TRUE),
    ('MSFT', 'Microsoft Corporation', 'Equity', 'USD', TRUE),
    ('GOOGL', 'Alphabet Inc.', 'Equity', 'USD', TRUE),
    ('US10Y', 'US Treasury 10-Year Note', 'Bond', 'USD', TRUE),
    ('VFIAX', 'Vanguard 500 Index Fund', 'Fund', 'USD', TRUE),
    ('CASH-USD', 'US Dollar Cash', 'Cash', 'USD', FALSE);

INSERT INTO accounts (account_id, holder_name, cash_balance, status, version) VALUES
    ('ACC-1001', 'Alice Johnson', 25000.00, 'ACTIVE', 0),
    ('ACC-1002', 'Bob Smith', 5400.50, 'ACTIVE', 0),
    ('ACC-1003', 'Carol Davis', 120000.00, 'ACTIVE', 0),
    ('ACC-1004', 'David Lee', 0.00, 'CLOSED', 0);

-- account_id is now the accounts.id surrogate key, resolved here via account_id lookup
INSERT INTO positions (account_id, symbol, quantity, average_cost) VALUES
    ('ACC-1001', 'AAPL', 50, 150.25),
    ('ACC-1001', 'US10Y', 10, 98.50),
    ('ACC-1002', 'MSFT', 20, 310.75),
    ('ACC-1003', 'GOOGL', 15, 2750.00),
    ('ACC-1003', 'VFIAX', 100, 420.10);

INSERT INTO orders (account_id, symbol, side, quantity, price, status) VALUES
    ('ACC-1001', 'AAPL', 'BUY', 50, 150.25, 'FILLED'),
    ('ACC-1002', 'MSFT', 'BUY', 20, 310.75, 'FILLED'),
    ('ACC-1003', 'GOOGL', 'BUY', 15, 2750.00, 'FILLED'),
    ('ACC-1001', 'AAPL', 'SELL', 10, 155.00, 'NEW'),
    ('ACC-1002', 'MSFT', 'BUY', 5, 305.00, 'CANCELLED');

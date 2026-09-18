INSERT INTO instruments (symbol, name, asset_class, currency, tradable) VALUES
    ('AAPL', 'Apple Inc.', 'Equity', 'USD', TRUE),
    ('MSFT', 'Microsoft Corporation', 'Equity', 'USD', TRUE),
    ('GOOGL', 'Alphabet Inc.', 'Equity', 'USD', TRUE),
    ('US10Y', 'US Treasury 10-Year Note', 'Bond', 'USD', TRUE),
    ('VFIAX', 'Vanguard 500 Index Fund', 'Fund', 'USD', TRUE),
    ('CASH-USD', 'US Dollar Cash', 'Cash', 'USD', FALSE)
ON CONFLICT (symbol) DO NOTHING;
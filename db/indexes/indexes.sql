CREATE INDEX IF NOT EXISTS idx_positions_account_id
    ON positions(account_id);

CREATE INDEX IF NOT EXISTS idx_positions_symbol 
    ON positions(symbol);

CREATE INDEX IF NOT EXISTS idx_orders_account_id 
    ON orders(account_id);

CREATE INDEX IF NOT EXISTS idx_orders_symbol 
    ON orders(symbol);
INSERT INTO orders (account_id, symbol, side, quantity, price, status) VALUES
    ('ACC-1001', 'AAPL', 'BUY', 50, 150.25, 'FILLED'),
    ('ACC-1002', 'MSFT', 'BUY', 20, 310.75, 'FILLED'),
    ('ACC-1003', 'GOOGL', 'BUY', 15, 2750.00, 'FILLED'),
    ('ACC-1001', 'AAPL', 'SELL', 10, 155.00, 'NEW'),
    ('ACC-1002', 'MSFT', 'BUY', 5, 305.00, 'CANCELLED');
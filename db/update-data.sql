-- Clears existing data and reinserts fresh data
TRUNCATE TABLE orders RESTART IDENTITY CASCADE;
TRUNCATE TABLE positions RESTART IDENTITY CASCADE;
TRUNCATE TABLE accounts RESTART IDENTITY CASCADE;
TRUNCATE TABLE instruments RESTART IDENTITY CASCADE;

\i seeds/instruments-data.sql
\i seeds/accounts-data.sql
\i seeds/positions-data.sql
\i seeds/orders-data.sql
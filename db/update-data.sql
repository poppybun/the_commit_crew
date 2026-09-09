-- Clears existing data and reinserts fresh data
TRUNCATE TABLE orders RESTART IDENTITY CASCADE;
TRUNCATE TABLE positions RESTART IDENTITY CASCADE;
TRUNCATE TABLE accounts RESTART IDENTITY CASCADE;
TRUNCATE TABLE instruments RESTART IDENTITY CASCADE;


\i /docker-entrypoint-initdb.d/db/seeds/instruments-data.sql
\i /docker-entrypoint-initdb.d/db/seeds/accounts-data.sql
\i /docker-entrypoint-initdb.d/db/seeds/positions-data.sql
\i /docker-entrypoint-initdb.d/db/seeds/orders-data.sql
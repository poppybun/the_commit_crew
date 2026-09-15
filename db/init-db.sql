-- The Commit Crew Database Initialization

-- This master script orchestrates the database setup in dependency order:

-- Step 1: Create tables with constraints
\i /docker-entrypoint-initdb.d/schema/instruments.sql
\i /docker-entrypoint-initdb.d/schema/accounts.sql
\i /docker-entrypoint-initdb.d/schema/positions.sql
\i /docker-entrypoint-initdb.d/schema/orders.sql

-- Step 2: Create indexes for performance
\i /docker-entrypoint-initdb.d/indexes/indexes.sql

-- Step 3: Populate with seed data
\i /docker-entrypoint-initdb.d/seeds/instruments-data.sql
\i /docker-entrypoint-initdb.d/seeds/accounts-data.sql
\i /docker-entrypoint-initdb.d/seeds/positions-data.sql
\i /docker-entrypoint-initdb.d/seeds/orders-data.sql
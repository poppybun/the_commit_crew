-- The Commit Crew Database Initialization

-- This master script orchestrates the database setup in dependency order:

-- Step 1: Create tables with constraints
\i schema/instruments.sql
\i schema/accounts.sql
\i schema/positions.sql
\i schema/orders.sql

-- Step 2: Create indexes for performance
\i indexes/indexes.sql

-- Step 3: Populate with seed data
\i seeds/instruments-data.sql
\i seeds/accounts-data.sql
\i seeds/positions-data.sql
\i seeds/orders-data.sql
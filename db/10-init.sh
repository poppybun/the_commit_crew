#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 -U postgres -d "$POSTGRES_DB" -f /docker-entrypoint-initdb.d/init-db.sql
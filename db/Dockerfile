FROM postgres:15

ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}

# Single init script that runs everything in proper order
RUN echo '#!/bin/bash\nset -e\n\n# Wait for PGDATA to be ready\nwhile [ ! -d "$PGDATA" ]; do sleep 0.1; done\n\n# Add IP restriction\necho "host all all 10.9.0.0/16 scram-sha-256" >> "$PGDATA/pg_hba.conf"\n' > /docker-entrypoint-initdb.d/00-init.sh \
    && chmod +x /docker-entrypoint-initdb.d/00-init.sh

# Copy entire db directory with schema, seeds, and indexes
COPY . /docker-entrypoint-initdb.d/

VOLUME /var/lib/postgresql/data

EXPOSE 5432

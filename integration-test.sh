#!/usr/bin/env bash
set -euo pipefail

# Accept image name from Jenkins (e.g., the-commit-crew:42)
APP_IMAGE="${1:-the-commit-crew:latest}"
ENV=${2:-dev}
ENV_FILE="${3:-.env.${ENV}}"

if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: $ENV_FILE not found"
  exit 1
fi

# Load environment variables
set -a
source "$ENV_FILE"
set +a

NETWORK=the-commit-crew_default  # Use the same network as Start Database stage
POSTGRES_CONTAINER=commitcrew-dev-db-1  # Correct container name from docker-compose
APP_CONTAINER=the-commit-crew-app
APP_PORT=8081

# Detect which postgres container exists and is running
if docker ps --filter "name=commitcrew-dev-db-1" --quiet >/dev/null 2>&1; then
  POSTGRES_CONTAINER=commitcrew-dev-db-1
elif docker ps --filter "name=the_commit_crew-db-1" --quiet >/dev/null 2>&1; then
  POSTGRES_CONTAINER=the_commit_crew-db-1
else
  POSTGRES_CONTAINER=commitcrew-dev-db-1  # Default to local
fi

echo "Using postgres container: $POSTGRES_CONTAINER"

# Try to use Jenkins network, fallback to creating one locally
if ! docker network inspect "$NETWORK" >/dev/null 2>&1; then
  echo "== Creating network (running locally) =="
  docker network create "$NETWORK" >/dev/null 2>&1 || true
fi

# If postgres container exists, try to connect it to the network
if docker ps --filter "name=${POSTGRES_CONTAINER}" --quiet >/dev/null 2>&1; then
  docker network connect "$NETWORK" "$POSTGRES_CONTAINER" >/dev/null 2>&1 || true
fi

cleanup() {
  echo "== Teardown =="
  docker rm -f "$APP_CONTAINER" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "== Stage: Run Container =="
docker run -d --name "$APP_CONTAINER" --network "$NETWORK" -p "$APP_PORT:$APP_PORT" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://${POSTGRES_CONTAINER}:5432/${POSTGRES_DB}" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD="${POSTGRES_PASSWORD}" \
  -e SPRING_PROFILES_ACTIVE=test \
  -e AUTH_ENABLED=false \
  "$APP_IMAGE"

sleep 2

echo "== Stage: Wait for Service Ready =="
for i in $(seq 1 60); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$APP_PORT/accounts/1" || true)
  if [ "$code" != "000" ]; then 
    echo "Service is ready (HTTP $code)"
    break
  fi
  if [ $i -lt 60 ]; then
    echo "Waiting... (attempt $i/60, got HTTP $code)"
    sleep 2
  fi
done

echo "== Stage: Test Account Retrieval =="
RESPONSE=$(curl -s "http://localhost:$APP_PORT/accounts/1")
echo "Full response: $RESPONSE"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$APP_PORT/accounts/1")
echo "HTTP code: $HTTP_CODE"
echo "$RESPONSE" | grep -q '"status":"ACTIVE"' || { echo "FAIL: account not found"; exit 1; }
echo "PASS: account retrieved from database"

echo "== Stage: Test Place Order - Validation =="
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://localhost:$APP_PORT/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d '{"symbol":"","quantity":-10,"price":0}')
if [ "$CODE" != "400" ]; then
  echo "FAIL: expected 400 for invalid data, got $CODE"
  exit 1
fi
echo "PASS: bean validation caught invalid request (400)"

echo "== Stage: Test Place Order - Success =="
ORDER_RESPONSE=$(curl -s -X POST "http://localhost:$APP_PORT/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d "{\"accountId\":1,\"symbol\":\"AAPL\",\"side\":\"BUY\",\"quantity\":1,\"price\":100.00,\"idempotencyKey\":\"order-$(date +%s%N)\"}")
echo "Order response: $ORDER_RESPONSE"

echo "== Stage: Application Logs =="
docker logs "$APP_CONTAINER" 2>&1 | tail -100

echo "$ORDER_RESPONSE" | grep -q '"status":"FILLED"' || { echo "FAIL: order was not created"; exit 1; }
echo "PASS: order placed and persisted"

echo "== Stage: Verify Data in Postgres =="
docker exec -e PGPASSWORD="${POSTGRES_PASSWORD}" "$POSTGRES_CONTAINER" psql -U postgres -d "${POSTGRES_DB}" -c \
  "SELECT account_id, symbol, quantity FROM orders WHERE account_id=1 AND symbol='AAPL';"

echo "== ALL STAGES PASSED =="
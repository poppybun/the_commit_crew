#!/usr/bin/env bash
set -euo pipefail

# Accept image name from Jenkins (e.g., the-commit-crew:42)
APP_IMAGE="${1:-the-commit-crew:latest}"
ENV=${2:-dev}
ENV_FILE=".env.${ENV}"

if [ ! -f "$ENV_FILE" ]; then
  echo "ERROR: $ENV_FILE not found"
  exit 1
fi

# Load environment variables
set -a
source "$ENV_FILE"
set +a

NETWORK=commit-crew-net
POSTGRES=commitcrew-dev-db-1
APP_CONTAINER=the-commit-crew-app
APP_PORT=8080

cleanup() {
  echo "== Teardown =="
  docker rm -f "$APP_CONTAINER" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "== Stage: Network =="
docker network create "$NETWORK" >/dev/null 2>&1 || true
docker network connect "$NETWORK" "$POSTGRES" >/dev/null 2>&1 || true

# Skip build - image already exists from Jenkins
echo "== Stage: Using pre-built image: $APP_IMAGE =="

echo "== Stage: Run Container =="
docker run -d --name "$APP_CONTAINER" --network "$NETWORK" -p "$APP_PORT:8080" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://$POSTGRES:5432/${POSTGRES_DB}" \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD="${POSTGRES_PASSWORD}" \
  "$APP_IMAGE"

sleep 2

echo "== Stage: Wait for Service Ready =="
for i in $(seq 1 30); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$APP_PORT/accounts/1" || true)
  if [ "$code" != "000" ]; then break; fi
  sleep 2
done

echo "== Stage: Test Account Retrieval =="
curl -s "http://localhost:$APP_PORT/accounts/1" | grep -q '"status":"ACTIVE"' || { echo "FAIL: account not found"; exit 1; }
echo "PASS: account retrieved from database"

echo "== Stage: Test Place Order - Validation =="
CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "http://localhost:$APP_PORT/accounts/1/orders" \
  -H "Content-Type: application/json" \
  -d '{"symbol":"","quantity":-10,"price":0}')
if [ "$CODE" != "400" ]; then
  echo "FAIL: expected 400 for invalid data, got $CODE"
  exit 1
fi
echo "PASS: bean validation caught invalid request (400)"

echo "== Stage: Test Place Order - Success =="
ORDER_RESPONSE=$(curl -s -X POST "http://localhost:$APP_PORT/accounts/1/orders" \
  -H "Content-Type: application/json" \
  -d '{"accountId":1,"symbol":"AAPL","side":"BUY","quantity":100,"price":150.00,"idempotencyKey":"order-001"}')
echo "Order response: $ORDER_RESPONSE"
echo "$ORDER_RESPONSE" | grep -q '"status":"PENDING"' || { echo "FAIL: order was not created"; exit 1; }
echo "PASS: order placed and persisted"

echo "== Stage: Verify Data in Postgres =="
docker exec -e PGPASSWORD="${POSTGRES_PASSWORD}" "$POSTGRES" psql -U postgres -d "${POSTGRES_DB}" -c \
  "SELECT account_id, symbol, quantity FROM orders WHERE account_id=1 AND symbol='AAPL';"

echo "== ALL STAGES PASSED =="
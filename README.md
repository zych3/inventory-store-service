# Inventory & Store — Online Game Services

A two-service backend system for managing player inventories, virtual currency, and in-game store purchases. 

**Stack:** Java 21 · Spring Boot 3.5 · PostgreSQL · Redis · SQS (LocalStack) · GitHub Actions

---

## Quick start

Prerequisites: Docker, Java 21, Git.

```bash
git clone https://github.com/zych3/inventory-store.git
cd inventory-store

# Start infrastructure
docker compose up -d

# Start player-service (terminal 1)
./gradlew :player-service:bootRun

# Start inventory-store-service (terminal 2)
./gradlew :inventory-store-service:bootRun
```

Swagger UI:
- Player service: http://localhost:8081/swagger-ui.html
- Inventory & store: http://localhost:8080/swagger-ui.html

Health checks:
- http://localhost:8081/actuator/health
- http://localhost:8080/actuator/health

---

## Try it: end-to-end purchase

Walk through a complete purchase flow using curl. 
```bash
# 1. Create a player
curl -s -X POST http://localhost:8081/api/v1/players \
  -H "Content-Type: application/json" \
  -d '{"displayName": "Aloy"}' | jq

# 2. Grant 1000 gold
curl -s -X POST http://localhost:8081/api/v1/wallets/{PLAYER_ID}/credit \
  -H "Content-Type: application/json" \
  -d '{"amount": 1000, "reason": "welcome_bonus", "idempotencyKey": "welcome-001"}' | jq

# 3. Seed an item in the catalog
curl -s -X POST http://localhost:8080/api/v1/admin/items \
  -H "Content-Type: application/json" \
  -d '{"sku": "SWORD_IRON", "name": "Iron Sword", "type": "COSMETIC"}' | jq

# 4. Create a store offer (300 gold, available now)
curl -s -X POST http://localhost:8080/api/v1/admin/offers \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": "{ITEM_ID}",
    "price": 300,
    "activeFrom": "2025-01-01T00:00:00Z"
  }' | jq

# 5. Purchase the sword
curl -s -X POST http://localhost:8080/api/v1/purchases \
  -H "Content-Type: application/json" \
  -H "X-Player-Id: {PLAYER_ID}" \
  -d '{"offerId": "{OFFER_ID}", "idempotencyKey": "buy-001"}' | jq
# → status: "COMPLETED"

# 6. Verify inventory (should contain Iron Sword, quantity 1)
curl -s http://localhost:8080/api/v1/inventories/{PLAYER_ID} | jq

# 7. Verify wallet (should be 700)
curl -s http://localhost:8081/api/v1/wallets/{PLAYER_ID} | jq

# 8. Retry the same purchase (idempotent — returns same result, no double charge)
curl -s -X POST http://localhost:8080/api/v1/purchases \
  -H "Content-Type: application/json" \
  -H "X-Player-Id: {PLAYER_ID}" \
  -d '{"offerId": "{OFFER_ID}", "idempotencyKey": "buy-001"}' | jq
# → same purchase ID, wallet still 700

# 9. Try to buy something you can't afford (should return 402)
curl -s -X POST http://localhost:8080/api/v1/purchases \
  -H "Content-Type: application/json" \
  -H "X-Player-Id: {PLAYER_ID}" \
  -d '{"offerId": "{OFFER_ID}", "idempotencyKey": "buy-expensive"}' | jq
# (if the offer costs more than remaining balance)
```

---

## API reference

Both services expose OpenAPI documentation via Swagger UI.

### player-service (:8081)

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/api/v1/players` | Create a player (also creates wallet with balance 0) |
| GET | `/api/v1/players/{id}` | Get player profile |
| GET | `/api/v1/wallets/{playerId}` | Get wallet balance |
| POST | `/api/v1/wallets/{playerId}/credit` | Add currency (idempotent) |
| POST | `/api/v1/wallets/{playerId}/debit` | Remove currency (idempotent, optimistic lock) |
| GET | `/api/v1/wallets/{playerId}/transactions` | Transaction ledger history |

### inventory-store-service (:8080)

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/api/v1/admin/items` | Create a catalog item |
| POST | `/api/v1/admin/offers` | Create a store offer |
| GET | `/api/v1/store/items` | List all catalog items |
| GET | `/api/v1/store/offers` | List currently active offers |
| POST | `/api/v1/purchases` | Execute a purchase (idempotent) |
| GET | `/api/v1/purchases/{id}` | Check purchase status |
| GET | `/api/v1/inventories/{playerId}` | List player's inventory entries |

---

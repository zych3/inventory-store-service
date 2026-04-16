package com.jzyskowski.inventorystore.player.api;

import com.jzyskowski.inventorystore.player.domain.CurrencyTransaction;

import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id, long amount, String reason,
                                  String idempotencyKey, long balanceAfter,
                                  Instant createdAt) {
    public static TransactionResponse from(CurrencyTransaction tx) {
        return new TransactionResponse(tx.getId(), tx.getAmount(), tx.getReason(),
                tx.getIdempotencyKey(), tx.getBalanceAfter(), tx.getCreatedAt());
    }
}
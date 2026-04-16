package com.jzyskowski.inventorystore.player.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "currency_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrencyTransaction {
    @Id
    private UUID id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false, length = 128)
    private String reason;

    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static CurrencyTransaction record(UUID playerId, long amount,
                                             String reason, String idempotencyKey,
                                             long balanceAfter) {
        CurrencyTransaction tx = new CurrencyTransaction();
        tx.id = UUID.randomUUID();
        tx.playerId = playerId;
        tx.amount = amount;
        tx.reason = reason;
        tx.idempotencyKey = idempotencyKey;
        tx.balanceAfter = balanceAfter;
        return tx;
    }
}

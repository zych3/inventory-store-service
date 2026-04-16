package com.jzyskowski.inventorystore.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {

    @Id
    private UUID id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "offer_id", nullable = false)
    private UUID offerId;

    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @Column(name = "price_paid", nullable = false)
    private Long pricePaid;

    @Column(name = "currency_code", nullable = false, length = 16)
    private String currencyCode;

    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PurchaseStatus status;

    @Column(name = "failure_reason", length = 256)
    private String failureReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public static Purchase initiate(UUID playerId, StoreOffer offer, String idempotencyKey) {
        Purchase p = new Purchase();
        p.id = UUID.randomUUID();
        p.playerId = playerId;
        p.offerId = offer.getId();
        p.itemId = offer.getItem().getId();
        p.pricePaid = offer.getPrice();
        p.currencyCode = offer.getCurrencyCode();
        p.idempotencyKey = idempotencyKey;
        p.status = PurchaseStatus.PENDING;
        return p;
    }

    public void complete() {
        if (this.status != PurchaseStatus.PENDING) {
            throw new IllegalStateException("Cannot complete purchase in status: " + status);
        }
        this.status = PurchaseStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void fail(String reason) {
        if (this.status != PurchaseStatus.PENDING) {
            throw new IllegalStateException("Cannot fail purchase in status: " + status);
        }
        this.status = PurchaseStatus.FAILED;
        this.failureReason = reason;
    }

    public void refund() {
        if (this.status != PurchaseStatus.COMPLETED) {
            throw new IllegalStateException("Cannot refund purchase in status: " + status);
        }
        this.status = PurchaseStatus.REFUNDED;
    }
}

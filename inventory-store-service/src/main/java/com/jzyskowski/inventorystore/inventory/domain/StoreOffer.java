package com.jzyskowski.inventorystore.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "store_offers")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoreOffer {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Long price;

    @Column(name = "currency_code", nullable = false, length = 16)
    private String currencyCode;

    @Column(name = "active_from", nullable = false)
    private Instant activeFrom;

    @Column(name = "active_until")
    private Instant activeUntil;

    @Column(name = "max_per_player")
    private Integer maxPerPlayer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public boolean isActiveAt(Instant now) {
        if (now.isBefore(activeFrom)) return false;
        if (activeUntil != null && now.isAfter(activeUntil)) return false;
        return true;
    }

    public static StoreOffer create(Item item, long price, String currencyCode,
                                    Instant activeFrom, Instant activeUntil,
                                    Integer maxPerPlayer) {
        return StoreOffer.builder()
                .id(UUID.randomUUID())
                .item(item)
                .price(price)
                .currencyCode(currencyCode)
                .activeFrom(activeFrom)
                .activeUntil(activeUntil)
                .maxPerPlayer(maxPerPlayer)
                .build();
    }
}

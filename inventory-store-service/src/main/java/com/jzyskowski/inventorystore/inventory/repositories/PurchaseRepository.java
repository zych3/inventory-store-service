package com.jzyskowski.inventorystore.inventory.repositories;

import com.jzyskowski.inventorystore.inventory.domain.Purchase;
import com.jzyskowski.inventorystore.inventory.domain.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

    Optional<Purchase> findByPlayerIdAndIdempotencyKey(UUID playerId, String idempotencyKey);

    long countByPlayerIdAndOfferId(UUID playerId, UUID offerId);

    List<Purchase> findByStatusAndCreatedAtBefore(PurchaseStatus status, Instant cutoff);
}

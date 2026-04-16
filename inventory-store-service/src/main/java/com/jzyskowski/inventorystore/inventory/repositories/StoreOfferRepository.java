package com.jzyskowski.inventorystore.inventory.repositories;

import com.jzyskowski.inventorystore.inventory.domain.StoreOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface StoreOfferRepository extends JpaRepository<StoreOffer, UUID> {

    @Query("""
            SELECT o FROM StoreOffer o JOIN FETCH o.item
            WHERE o.activeFrom <= :now
            AND (o.activeUntil IS NULL OR o.activeUntil > :now)
            """)
    List<StoreOffer> findActiveOffers(Instant now);
}
package com.jzyskowski.inventorystore.inventory.repositories;

import com.jzyskowski.inventorystore.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    @Query("SELECT i FROM Inventory i LEFT JOIN FETCH i.entries WHERE i.playerId = :playerId")
    Optional<Inventory> findByPlayerIdWithEntries(UUID playerId);
}

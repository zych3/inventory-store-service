package com.jzyskowski.inventorystore.inventory.repositories;

import com.jzyskowski.inventorystore.inventory.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findBySku(String sku);
}
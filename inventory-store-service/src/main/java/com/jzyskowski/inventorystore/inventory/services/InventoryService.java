package com.jzyskowski.inventorystore.inventory.services;

import com.jzyskowski.inventorystore.inventory.domain.Inventory;
import com.jzyskowski.inventorystore.inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public Inventory getOrCreateInventory(UUID playerId) {
        return inventoryRepository.findByPlayerIdWithEntries(playerId)
                .orElseGet(() -> inventoryRepository.save(Inventory.createForPlayer(playerId)));
    }

    @Transactional(readOnly = true)
    public Inventory getInventory(UUID playerId) {
        return inventoryRepository.findByPlayerIdWithEntries(playerId)
                .orElseThrow(() -> new InventoryNotFoundException(playerId));
    }
}

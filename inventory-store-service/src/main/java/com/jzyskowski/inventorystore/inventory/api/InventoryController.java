package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{playerId}")
    public InventoryResponse getInventory(@PathVariable UUID playerId) {
        return InventoryResponse.from(inventoryService.getInventory(playerId));
    }
}

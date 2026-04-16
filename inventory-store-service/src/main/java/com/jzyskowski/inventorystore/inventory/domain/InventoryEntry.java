package com.jzyskowski.inventorystore.inventory.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "inventory_entries")
@IdClass(InventoryEntryId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryEntry {

    @Id
    @Column(name = "inventory_player_id")
    private UUID inventoryPlayerId;

    @Id
    @Column(name = "item_id")
    private UUID itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_player_id", insertable = false, updatable = false)
    private Inventory inventory;

    @Column(nullable = false)
    private int quantity;

    public static InventoryEntry create(Inventory inventory, UUID itemId, int quantity) {
        InventoryEntry entry = new InventoryEntry();
        entry.inventoryPlayerId = inventory.getPlayerId();
        entry.inventory = inventory;
        entry.itemId = itemId;
        entry.quantity = quantity;
        return entry;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.quantity += amount;
    }

    public void removeQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (this.quantity < amount) throw new IllegalStateException(
                "Cannot remove %d, only %d available".formatted(amount, quantity));
        this.quantity -= amount;
    }
}

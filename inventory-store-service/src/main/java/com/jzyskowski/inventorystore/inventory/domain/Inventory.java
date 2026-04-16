package com.jzyskowski.inventorystore.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @Column(name = "player_id")
    private UUID playerId;

    @Version
    @Column(nullable = false)
    private Long version;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryEntry> entries = new ArrayList<>();

    public static Inventory createForPlayer(UUID playerId) {
        Inventory inv = new Inventory();
        inv.playerId = playerId;
        return inv;
    }

    public void grantItem(Item item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        Optional<InventoryEntry> existing = entries.stream()
                .filter(e -> e.getItemId().equals(item.getId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().addQuantity(quantity);
        } else {
            entries.add(InventoryEntry.create(this, item.getId(), quantity));
        }
    }

    public void removeItem(UUID itemId, int quantity) {
        InventoryEntry entry = entries.stream()
                .filter(e -> e.getItemId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Item not in inventory: " + itemId));

        entry.removeQuantity(quantity);
    }
}

package com.jzyskowski.inventorystore.inventory.services;

import com.jzyskowski.inventorystore.inventory.domain.Item;
import com.jzyskowski.inventorystore.inventory.domain.ItemType;
import com.jzyskowski.inventorystore.inventory.domain.StoreOffer;
import com.jzyskowski.inventorystore.inventory.repositories.ItemRepository;
import com.jzyskowski.inventorystore.inventory.repositories.StoreOfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ItemRepository itemRepository;
    private final StoreOfferRepository storeOfferRepository;

    @Transactional
    public Item createItem(String sku, String name, ItemType type, Map<String, Object> metadata) {
        return itemRepository.save(Item.create(sku, name, type, metadata));
    }

    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional
    public StoreOffer createOffer(UUID itemId, long price, String currencyCode,
                                  Instant activeFrom, Instant activeUntil,
                                  Integer maxPerPlayer) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        return storeOfferRepository.save(
                StoreOffer.create(item, price, currencyCode, activeFrom, activeUntil, maxPerPlayer));
    }

    @Transactional(readOnly = true)
    public List<StoreOffer> getActiveOffers() {
        return storeOfferRepository.findActiveOffers(Instant.now());
    }
}

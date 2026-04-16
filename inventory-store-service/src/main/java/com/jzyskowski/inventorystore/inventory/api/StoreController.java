package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.services.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

    private final CatalogService catalogService;

    @GetMapping("/items")
    public List<ItemResponse> getItems() {
        return catalogService.getAllItems().stream()
                .map(ItemResponse::from)
                .toList();
    }

    @GetMapping("/offers")
    public List<OfferResponse> getActiveOffers() {
        return catalogService.getActiveOffers().stream()
                .map(OfferResponse::from)
                .toList();
    }
}
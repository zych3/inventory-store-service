package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.services.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CatalogService catalogService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody CreateItemRequest request) {
        return ItemResponse.from(
                catalogService.createItem(request.sku(), request.name(),
                        request.type(), request.metadata()));
    }

    @PostMapping("/offers")
    @ResponseStatus(HttpStatus.CREATED)
    public OfferResponse createOffer(@Valid @RequestBody CreateOfferRequest request) {
        return OfferResponse.from(
                catalogService.createOffer(request.itemId(), request.price(),
                        request.currencyCode() != null ? request.currencyCode() : "SOFT",
                        request.activeFrom(), request.activeUntil(), request.maxPerPlayer()));
    }
}

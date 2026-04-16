package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.services.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse purchase(
            @RequestHeader("X-Player-Id") UUID playerId,
            @Valid @RequestBody PurchaseRequest request) {
        return PurchaseResponse.from(
                purchaseService.initiatePurchase(playerId, request.offerId(),
                        request.idempotencyKey()));
    }

    @GetMapping("/{purchaseId}")
    public PurchaseResponse getPurchase(@PathVariable UUID purchaseId) {
        return PurchaseResponse.from(purchaseService.getPurchase(purchaseId));
    }
}

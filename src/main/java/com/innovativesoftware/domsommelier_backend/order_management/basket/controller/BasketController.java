package com.innovativesoftware.domsommelier_backend.order_management.basket.controller;

import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @GetMapping("/{customerId}")
    public ResponseEntity<BasketDto> getBasket(@PathVariable UUID customerId) {
        return ResponseEntity.ok(basketService.getBasket(customerId));
    }

    @PostMapping("/{customerId}/add/{productId}")
    public ResponseEntity<BasketDto> addItem(@PathVariable UUID customerId,
                                             @PathVariable UUID productId,
                                             @RequestParam int quantity) {
        return ResponseEntity.ok(basketService.addItem(customerId, productId, quantity));
    }

    @DeleteMapping("/{customerId}/remove/{productId}")
    public ResponseEntity<BasketDto> removeItem(@PathVariable UUID customerId, @PathVariable UUID productId) {
        return ResponseEntity.ok(basketService.removeItem(customerId, productId));
    }

    @PostMapping("/{customerId}/clear")
    public ResponseEntity<Void> clearBasket(@PathVariable UUID customerId) {
        basketService.clearBasket(customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{customerId}/promo/{promoId}")
    public ResponseEntity<BasketDto> applyPromo(@PathVariable UUID customerId, @PathVariable UUID promoId) {
        return ResponseEntity.ok(basketService.applyPromo(customerId, promoId));
    }

    @DeleteMapping("/{customerId}/promo")
    public ResponseEntity<BasketDto> removePromo(@PathVariable UUID customerId) {
        return ResponseEntity.ok(basketService.removePromo(customerId));
    }

    @PostMapping("/{customerId}/update/{productId}")
    public ResponseEntity<BasketDto> updateQuantity(@PathVariable UUID customerId,
                                                    @PathVariable UUID productId,
                                                    @RequestParam int quantity) {
        return ResponseEntity.ok(basketService.updateQuantity(customerId, productId, quantity));
    }

    @PostMapping("/{customerId}/checkout/{addressId}")
    public ResponseEntity<UUID> checkout(
            @PathVariable UUID customerId,
            @PathVariable UUID addressId
    ) {
        UUID orderId = basketService.checkoutBasket(customerId, addressId);
        return ResponseEntity.ok(orderId);
    }
}

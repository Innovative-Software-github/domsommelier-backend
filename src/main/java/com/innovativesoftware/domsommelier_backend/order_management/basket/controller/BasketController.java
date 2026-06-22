package com.innovativesoftware.domsommelier_backend.order_management.basket.controller;

import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.CheckoutRequestDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.StoreAvailabilityDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.service.BasketService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @GetMapping("/{customerId}")
    public ResponseEntity<BasketDto> getBasket(@PathVariable UUID customerId) {
        return ResponseEntity.ok(basketService.getBasket(customerId));
    }

    @GetMapping("/{customerId}/availability")
    public ResponseEntity<List<StoreAvailabilityDto>> getStoreAvailability(@PathVariable UUID customerId) {
        return ResponseEntity.ok(basketService.getStoreAvailability(customerId));
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

    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<Void> clearBasketDelete(@PathVariable UUID customerId) {
        basketService.clearBasket(customerId);
        return ResponseEntity.ok().build();
    }

    @Hidden
    @Deprecated
    @PostMapping("/{customerId}/promo/{promoId}")
    public ResponseEntity<BasketDto> applyPromo(@PathVariable UUID customerId, @PathVariable UUID promoId) {
        return ResponseEntity.ok(basketService.applyPromo(customerId, promoId));
    }

    @Hidden
    @Deprecated
    @DeleteMapping("/{customerId}/promo")
    public ResponseEntity<BasketDto> removePromo(@PathVariable UUID customerId) {
        return ResponseEntity.ok(basketService.removePromo(customerId));
    }

    @Hidden
    @PostMapping("/{customerId}/update/{productId}")
    public ResponseEntity<BasketDto> updateQuantity(@PathVariable UUID customerId,
                                                    @PathVariable UUID productId,
                                                    @RequestParam int quantity) {
        return ResponseEntity.ok(basketService.updateQuantity(customerId, productId, quantity));
    }

    @PostMapping("/{customerId}/checkout/{wineStoreId}")
    public ResponseEntity<UUID> checkout(
            @PathVariable UUID customerId,
            @PathVariable Long wineStoreId,
            @RequestBody(required = false) CheckoutRequestDto checkoutData
    ) {
        UUID orderId = basketService.checkoutBasket(customerId, wineStoreId, checkoutData);
        return ResponseEntity.ok(orderId);
    }
}

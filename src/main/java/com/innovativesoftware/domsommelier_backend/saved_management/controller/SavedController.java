package com.innovativesoftware.domsommelier_backend.saved_management.controller;

import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedDto;
import com.innovativesoftware.domsommelier_backend.saved_management.service.SavedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
public class SavedController {

    private final SavedService savedService;

    @GetMapping("/{customerId}")
    public ResponseEntity<SavedDto> getSaved(@PathVariable UUID customerId) {
        return ResponseEntity.ok(savedService.getSaved(customerId));
    }

    @PostMapping("/{customerId}/add/{productId}")
    public ResponseEntity<SavedDto> addItem(@PathVariable UUID customerId,
                                             @PathVariable UUID productId) {
        return ResponseEntity.ok(savedService.addItem(customerId, productId));
    }

    @DeleteMapping("/{customerId}/remove/{productId}")
    public ResponseEntity<SavedDto> removeItem(@PathVariable UUID customerId,
                                               @PathVariable UUID productId) {
        return ResponseEntity.ok(savedService.removeItem(customerId, productId));
    }

    @PostMapping("/{customerId}/clear")
    public ResponseEntity<Void> clearBasket(@PathVariable UUID customerId) {
        savedService.clearSaved(customerId);
        return ResponseEntity.ok().build();
    }
}

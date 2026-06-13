package com.innovativesoftware.domsommelier_backend.saved_management.controller;

import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedDto;
import com.innovativesoftware.domsommelier_backend.saved_management.service.SavedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
@Tag(name = "saved-controller", description = "Управление избранными товарами")
public class SavedController {

    private final SavedService savedService;

    @GetMapping("/{customerId}")
    @Operation(summary = "Получить список избранного")
    public ResponseEntity<SavedDto> getSaved(@PathVariable UUID customerId) {
        return ResponseEntity.ok(savedService.getSaved(customerId));
    }

    @PostMapping("/{customerId}/add/{productId}")
    @Operation(summary = "Добавить товар в избранное")
    public ResponseEntity<SavedDto> addItem(@PathVariable UUID customerId,
                                             @PathVariable UUID productId) {
        return ResponseEntity.ok(savedService.addItem(customerId, productId));
    }

    @DeleteMapping("/{customerId}/remove/{productId}")
    @Operation(summary = "Удалить товар из избранного")
    public ResponseEntity<SavedDto> removeItem(@PathVariable UUID customerId,
                                               @PathVariable UUID productId) {
        return ResponseEntity.ok(savedService.removeItem(customerId, productId));
    }

    @PostMapping("/{customerId}/clear")
    @Operation(summary = "Очистить избранное")
    public ResponseEntity<Void> clearSaved(@PathVariable UUID customerId) {
        savedService.clearSaved(customerId);
        return ResponseEntity.ok().build();
    }
}

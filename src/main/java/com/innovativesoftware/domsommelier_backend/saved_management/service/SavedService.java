package com.innovativesoftware.domsommelier_backend.saved_management.service;

import com.innovativesoftware.domsommelier_backend.infrastructure.RedisService;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SavedService {
    
    private final RedisService redisService;
    private final ProductRepository productRepository;

    private String savedKey(UUID customerId) {
        return "saved:" + customerId;
    }
    
    public SavedDto getSaved(UUID customerId) {
        Object obj = redisService.getObject(savedKey(customerId));
        if (obj instanceof SavedDto savedDto) {
            return savedDto;
        }
        return SavedDto.builder().customerId(customerId).build();
    }
    
    public SavedDto addItem(UUID customerId, UUID productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        SavedDto saved = getSaved(customerId);
        List<UUID> updatedItems = new ArrayList<>(saved.getItems());
        if (updatedItems.contains(productId)) {
            throw new IllegalArgumentException("Product already in saved");
        }
        updatedItems.add(productId);

        saved.setItems(updatedItems);
        redisService.save(savedKey(customerId), saved);
        return saved;
    }

    public SavedDto removeItem(UUID customerId, UUID productId) {
        SavedDto saved = getSaved(customerId);
        List<UUID> updatedItems = new ArrayList<>(saved.getItems());
        if (!updatedItems.contains(productId)) {
            throw new NoSuchElementException("Product not found in saved");
        }
        updatedItems.remove(productId);
        saved.setItems(updatedItems);
        return saved;
    }

    public void clearSaved(UUID customerId) {
        redisService.remove(savedKey(customerId));
    }
}

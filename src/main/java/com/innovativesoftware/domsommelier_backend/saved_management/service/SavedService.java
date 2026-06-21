package com.innovativesoftware.domsommelier_backend.saved_management.service;

import com.innovativesoftware.domsommelier_backend.infrastructure.RedisService;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls;
import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedDto;
import com.innovativesoftware.domsommelier_backend.saved_management.model.SavedItemDto;
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
        SavedDto savedDto = redisService.getObject(savedKey(customerId), SavedDto.class);
        if (savedDto != null) {
            return savedDto;
        }
        return SavedDto.builder().customerId(customerId).build();
    }

    public SavedDto addItem(UUID customerId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        SavedDto saved = getSaved(customerId);
        List<SavedItemDto> updatedItems = new ArrayList<>(saved.getItems());

        boolean alreadySaved = updatedItems.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
        if (alreadySaved) {
            throw new IllegalArgumentException("Product already in saved");
        }

        SavedItemDto newItem = SavedItemDto.builder()
                .product(SavedItemDto.SavedProductDto.builder()
                        .id(productId)
                        .name(product.getName())
                        .article(product.getArticle())
                        .price(product.getPrice())
                        .discount(product.getDiscount())
                        .productCountry(product.getProductCountry().getName())
                        .productCategoryName(product.getProductCategory().getName().name())
                        .productPhoto(ProductPhotoUrls.toFileDtos(product))
                        .build())
                .build();

        updatedItems.add(newItem);
        saved.setItems(updatedItems);
        redisService.save(savedKey(customerId), saved);
        return saved;
    }

    public SavedDto removeItem(UUID customerId, UUID productId) {
        SavedDto saved = getSaved(customerId);
        List<SavedItemDto> updatedItems = new ArrayList<>(saved.getItems());

        boolean removed = updatedItems.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new NoSuchElementException("Product not found in saved");
        }

        saved.setItems(updatedItems);
        redisService.save(savedKey(customerId), saved);
        return saved;
    }

    public void clearSaved(UUID customerId) {
        redisService.remove(savedKey(customerId));
    }
}

package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineCardDtoMapper implements ProductCardDtoMapper {

    private final WineRepository wineRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        Wine wine = wineRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Wine not found for product " + product.getId()));
        return WineCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .article(product.getArticle())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(product.getProductCategory().getName().name())
                .productPhoto(product.getProductPhoto().stream().map(photo -> FileDTO.builder()
                        .id(photo.getId())
                        .bucket(photo.getBucket())
                        .name(photo.getName())
                        .description(photo.getDescription())
                        .build()).toList())
                .volume(wine.getVolume())
                .color(WineCardDto.Color.valueOf(wine.getColor().getName().name()))
                .type(WineCardDto.Type.valueOf(wine.getType().getName().name()))
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return "wine";
    }
}

package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import org.springframework.stereotype.Component;

@Component
public class DefaultProductCardDtoMapper implements ProductCardDtoMapper {
    @Override
    public ProductCardDto toCardDto(Product product) {
        return ProductCardDto.builder()
                .id(product.getId()).name(product.getName()).article(product.getArticle())
                .productCategoryName(String.valueOf(product.getProductCategory().getName()))
                .discount(product.getDiscount()).price(product.getPrice()).productPhoto(
                        product.getProductPhoto().stream().map(photo -> FileDTO.builder()
                                .id(photo.getId())
                                .bucket(photo.getBucket())
                                .name(photo.getName())
                                .description(photo.getDescription())
                                .build()).toList()
                )
                .productCountry(product.getProductCountry().getName()).build();
    }

    @Override
    public String getSupportedCategory() {
        return "";
    }
}

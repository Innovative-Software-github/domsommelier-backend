package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;

public interface ProductCardDtoMapper {
    ProductCardDto toCardDto(Product product);

    String getSupportedCategory();
}

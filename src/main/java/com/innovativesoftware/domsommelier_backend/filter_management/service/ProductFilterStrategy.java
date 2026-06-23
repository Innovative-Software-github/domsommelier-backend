package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ProductFilterStrategy {
    Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable);
    ProductCategoryEnum getCategoryEnum();
}

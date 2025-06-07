package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductFilterStrategy {
    List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable);
}

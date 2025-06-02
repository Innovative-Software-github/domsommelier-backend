package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;

public interface ProductDetailsMapper<T> {
    boolean supports(ProductCategoryEnum category);
    Object mapDetails(Product product);
}

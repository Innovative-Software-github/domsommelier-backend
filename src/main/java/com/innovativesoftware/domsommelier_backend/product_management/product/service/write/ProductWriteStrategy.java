package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;

import java.util.UUID;

/**
 * Стратегия записи товара одной категории. Реестр выбирает реализацию по {@link #supports}.
 * Зеркалит read-side ({@code ProductDetailsMapperRegistry}, {@code ProductFilterStrategyFactory}).
 */
public interface ProductWriteStrategy {

    boolean supports(ProductCategoryEnum category);

    Product create(ProductWriteRequest request);

    Product update(UUID id, ProductWriteRequest request);

    void delete(UUID id);
}

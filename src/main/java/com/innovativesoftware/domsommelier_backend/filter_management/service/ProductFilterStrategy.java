package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductFacetsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Map;

public interface ProductFilterStrategy {
    Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable);

    /** Сколько товаров даст каждый вариант перечисленных multi_select-полей, см. FacetCounter. */
    ProductFacetsDto facets(Map<String, Object> params, Collection<String> fields);
    ProductCategoryEnum getCategoryEnum();
}

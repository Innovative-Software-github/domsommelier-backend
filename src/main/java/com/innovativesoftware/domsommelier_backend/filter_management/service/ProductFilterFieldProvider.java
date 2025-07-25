package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;

import java.util.Map;
import java.util.Set;

public interface ProductFilterFieldProvider {
    ProductCategoryEnum getSupportedCategory();
    Map<String, String> getFieldRuNames(); // field -> русское имя
    Set<String> getLabels(String field);
}

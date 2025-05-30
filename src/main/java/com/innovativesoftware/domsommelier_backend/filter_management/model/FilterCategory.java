package com.innovativesoftware.domsommelier_backend.filter_management.model;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FilterCategory {
    private ProductCategoryEnum category;
    public List<FilterDto> filters;
}

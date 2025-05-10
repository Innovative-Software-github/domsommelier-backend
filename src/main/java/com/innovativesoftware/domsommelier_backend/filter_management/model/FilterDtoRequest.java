package com.innovativesoftware.domsommelier_backend.filter_management.model;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDtoRequest {
    private UUID id;
    private String name;
    private String field;
    private ProductCategories productCategory;
    private FilterType filterType;
    private List<FilterOptionDtoRequest> options;
}

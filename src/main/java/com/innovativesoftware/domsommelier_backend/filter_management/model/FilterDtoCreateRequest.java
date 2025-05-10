package com.innovativesoftware.domsommelier_backend.filter_management.model;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDtoCreateRequest {
    private String name;
    private String field;
    private ProductCategories productCategory;
    private FilterType filterType;
    @Schema(description = "можно пустым списком для создания типа RANGE")
    private List<String> options;
}

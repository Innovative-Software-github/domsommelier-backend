package com.innovativesoftware.domsommelier_backend.filter_management.model;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class FilterDto {
    /** null keeps the legacy label-based request contract. */
    private String selectionMode;
    /** whisky/cognac: visible only when exactly that subtype is selected. */
    private String subtype;
    private UUID id;
    private FilterType type;
    private String name;
    private String field;
    private ProductCategoryEnum category;

    public FilterDto(HashMap<String, Object> obj) {
        //this.id = UUID.fromString((String) obj.get("id"));
        this.name = (String) obj.get("name");
        this.field = (String) obj.get("field");
        this.category = ProductCategoryEnum.valueOf((String) obj.get("category"));
        this.type = FilterType.valueOf((String) obj.get("type"));
    }
}

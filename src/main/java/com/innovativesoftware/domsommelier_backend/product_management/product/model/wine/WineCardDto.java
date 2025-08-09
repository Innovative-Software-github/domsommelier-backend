package com.innovativesoftware.domsommelier_backend.product_management.product.model.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class WineCardDto extends ProductCardDto {
    private String volume;
    private String color;
    private String type;
}

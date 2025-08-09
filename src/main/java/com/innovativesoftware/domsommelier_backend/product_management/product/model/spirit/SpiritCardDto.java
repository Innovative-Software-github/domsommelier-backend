package com.innovativesoftware.domsommelier_backend.product_management.product.model.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class SpiritCardDto extends ProductCardDto {
    private String category;
    private String strength;
    private String volume;
}

package com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class ShampaigneAndSparklingCardDto extends ProductCardDto {

    private String category;
    private String content;
    private String color;
    private String volume;
}

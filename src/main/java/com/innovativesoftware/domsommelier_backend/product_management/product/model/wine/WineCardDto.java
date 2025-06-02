package com.innovativesoftware.domsommelier_backend.product_management.product.model.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class WineCardDto extends ProductCardDto {
    enum Color {RED, WHITE, PINK;}
    enum Type { SWEET, SEMISWEET, DRY }

    private Color color;
    private Type type;
}

package com.innovativesoftware.domsommelier_backend.product_management.product.model.snack;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class SnackCardDto extends ProductCardDto {
    private String category;
}

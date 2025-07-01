package com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShampaigneAndSparklingDetailsDto {
    private String category;
    private String content;
    private String color;
    private String volume;
}

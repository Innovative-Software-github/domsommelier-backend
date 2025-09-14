package com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShampaigneAndSparklingDetailsDto {
    private String subcategory;
    private String content;
    private String color;
    private String volume;
    private String producer;
    private List<String> features;
}

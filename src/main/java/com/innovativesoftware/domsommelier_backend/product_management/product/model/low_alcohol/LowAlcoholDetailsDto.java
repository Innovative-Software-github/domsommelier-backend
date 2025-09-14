package com.innovativesoftware.domsommelier_backend.product_management.product.model.low_alcohol;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LowAlcoholDetailsDto {
    private String subcategory;
    private String producer;
    private String volume;
    private String strength;
    private List<String> features;
}

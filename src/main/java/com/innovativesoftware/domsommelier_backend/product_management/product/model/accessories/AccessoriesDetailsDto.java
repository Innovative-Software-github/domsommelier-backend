package com.innovativesoftware.domsommelier_backend.product_management.product.model.accessories;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AccessoriesDetailsDto {
    private String producer;
    private List<String> features;
}

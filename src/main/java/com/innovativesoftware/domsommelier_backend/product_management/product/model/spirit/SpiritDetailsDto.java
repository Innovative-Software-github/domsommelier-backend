package com.innovativesoftware.domsommelier_backend.product_management.product.model.spirit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SpiritDetailsDto {
    @com.fasterxml.jackson.annotation.JsonUnwrapped
    private com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.SpiritAttributes extendedDetails;

    private String category;
    private String strength;
    private String producer;
    private String volume;
    private List<String> features;
}

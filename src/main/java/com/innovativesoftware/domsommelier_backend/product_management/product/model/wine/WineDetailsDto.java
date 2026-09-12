package com.innovativesoftware.domsommelier_backend.product_management.product.model.wine;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class WineDetailsDto {
    @com.fasterxml.jackson.annotation.JsonUnwrapped
    private com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.WineAttributes extendedDetails;

    private Integer productionYear;
    private String color;
    private String type;
    private List<String> grapes;
    private String producer;
    private String volume;
    private List<String> features;
}

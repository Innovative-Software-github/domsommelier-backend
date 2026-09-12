package com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ShampaigneAndSparklingDetailsDto {
    @com.fasterxml.jackson.annotation.JsonUnwrapped
    private com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.SparklingAttributes extendedDetails;

    private String subcategory;
    private String content;
    private String color;
    private String volume;
    private String producer;
    private List<String> features;

    /** Derived labels; composition is the single source of truth for new sparkling products. */
    public List<String> getGrapes() {
        return extendedDetails == null || extendedDetails.grapeComposition() == null ? null
            : extendedDetails.grapeComposition().stream().map(share -> share.grape().label()).toList();
    }
}

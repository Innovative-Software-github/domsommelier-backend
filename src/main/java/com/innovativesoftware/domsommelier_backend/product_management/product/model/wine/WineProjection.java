package com.innovativesoftware.domsommelier_backend.product_management.product.model.wine;

import java.util.UUID;

public interface WineProjection {
    UUID getId();
    String getName();
    Integer getPrice();
    Integer getDiscount();
}

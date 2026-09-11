package com.innovativesoftware.domsommelier_backend.product_management.product.model.wine;

import java.math.BigDecimal;
import java.util.UUID;

public interface WineProjection {
    UUID getId();
    String getName();
    Integer getPrice();
    BigDecimal getSalePrice();
}

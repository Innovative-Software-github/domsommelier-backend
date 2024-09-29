package com.innovativesoftware.domsommelier_backend.product.model;

import java.util.UUID;

public interface ProductNewProjection {
    UUID getProductId();
    Integer getAmountOfDelivery();
    Integer getCommonAmount();
}

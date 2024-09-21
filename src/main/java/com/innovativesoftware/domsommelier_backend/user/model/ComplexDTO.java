package com.innovativesoftware.domsommelier_backend.user.model;

import java.util.UUID;

public interface ComplexDTO {
    UUID getProductId();
    UUID getCustomerId();
    Integer getOrderedCount();
}

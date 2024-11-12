package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import java.util.UUID;

public interface ProductPhotoProjection {
    UUID getId();
    String getBucket();
    String getFileName();
}

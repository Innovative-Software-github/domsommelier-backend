package com.innovativesoftware.domsommelier_backend.product.model;

import java.util.UUID;

public interface ProductPhotoProjection {
    UUID getId();
    String getBucket();
    String getFileName();
}

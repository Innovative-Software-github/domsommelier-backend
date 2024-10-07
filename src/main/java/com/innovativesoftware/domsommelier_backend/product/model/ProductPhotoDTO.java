package com.innovativesoftware.domsommelier_backend.product.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ProductPhotoDTO implements ProductPhotoProjection, Serializable {
    private UUID id;
    private String bucket;
    private String fileName;
}

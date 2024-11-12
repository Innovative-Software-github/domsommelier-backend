package com.innovativesoftware.domsommelier_backend.product_management.product.model;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
public class WineWithPhotoDTO implements WineProjection {
    private UUID id;
    private String name;
    private Integer price;
    private Integer discount;
    private List<ProductPhotoProjection> productPhotos;
}

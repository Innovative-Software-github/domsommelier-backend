package com.innovativesoftware.domsommelier_backend.product.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WineWithPhotoDTO extends WineDTO {
    private List<ProductPhotoProjection> productPhotos;
}

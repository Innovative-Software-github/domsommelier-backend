package com.innovativesoftware.domsommelier_backend.product.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class WineDTO implements WineProjection, Serializable {
    private UUID id;
    private String name;
    private Integer price;
    private Integer discount;
}

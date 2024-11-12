package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ProductNewDTO implements ProductNewProjection, Serializable {
    private UUID productId;
    private Integer amountOfDelivery;
    private Integer commonAmount;
}

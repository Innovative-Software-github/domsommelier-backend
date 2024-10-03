package com.innovativesoftware.domsommelier_backend.customer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class CustomerRecommendationsDTO implements CustomerRecommendationsProjection, Serializable {
    private UUID customerId;
    private UUID productId;
    private Integer priority;
    private Integer productPrice;
    private String productName;
    private String productArticle;
}

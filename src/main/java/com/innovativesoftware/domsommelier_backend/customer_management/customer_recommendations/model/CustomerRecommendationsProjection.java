package com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.model;

import java.util.UUID;

public interface CustomerRecommendationsProjection {
    UUID getProductId();
    UUID getCustomerId();
    Integer getPriority();
    Integer getProductPrice();
    String getProductName();
    String getProductArticle();
}

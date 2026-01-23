package com.innovativesoftware.domsommelier_backend.order_management.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderedProductDto {
    private UUID productId;
    private String name;
    private String article;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal sum;
}
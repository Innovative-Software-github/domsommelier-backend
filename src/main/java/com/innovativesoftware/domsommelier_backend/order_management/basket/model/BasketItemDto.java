package com.innovativesoftware.domsommelier_backend.order_management.basket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemDto implements Serializable {
    private UUID productId;
    private String productName;    // Имя продукта
    private BigDecimal price;      // Цена за штуку
    private Integer quantity;
}
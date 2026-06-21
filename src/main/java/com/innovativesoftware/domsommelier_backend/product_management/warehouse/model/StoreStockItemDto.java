package com.innovativesoftware.domsommelier_backend.product_management.warehouse.model;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Позиция склада винотеки: товар + его остаток в этой винотеке (0, если строки остатка нет). */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreStockItemDto {
    private UUID productId;
    private String article;
    private String name;
    private ProductCategoryEnum category;
    private BigDecimal price;
    private int quantity;
}

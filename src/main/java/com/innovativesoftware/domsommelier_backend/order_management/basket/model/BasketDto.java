package com.innovativesoftware.domsommelier_backend.order_management.basket.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto implements Serializable {
    private UUID customerId;
    @Singular
    private List<BasketItemDto> items;
    private UUID promoId;

    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO; // Итоговая цена
    @Builder.Default
    private Integer discount = 0; // В процентах
    @Builder.Default
    private BigDecimal discountedPrice = BigDecimal.ZERO; // Итоговая после скидки
}
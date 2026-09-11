package com.innovativesoftware.domsommelier_backend.order_management.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderFullDto {
    private UUID id;
    private OffsetDateTime date;
    private String statusName;
    private String pickupAddress;
    /** Сумма к оплате — после всех скидок. */
    private BigDecimal totalAmount;

    // Снапшот скидок на момент оформления (у старых заказов — null)
    /** Сумма позиций по прайсу, до скидок. */
    private BigDecimal itemsTotal;
    /** Экономия за счёт акционных цен. */
    private BigDecimal saleDiscountAmount;
    private Integer personalDiscountPercent;
    private BigDecimal personalDiscountAmount;

    private List<OrderedProductDto> items;
    private String customerPhone;
    private String customerName;
    private LocalDate pickupDate;
    private String paymentMethod;

    // Реквизиты винотеки самовывоза
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private String storeWorkingHours;
}
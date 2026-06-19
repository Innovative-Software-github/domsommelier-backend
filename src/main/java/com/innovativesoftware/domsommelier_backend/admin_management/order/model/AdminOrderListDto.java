package com.innovativesoftware.domsommelier_backend.admin_management.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminOrderListDto {
    private UUID id;
    private OffsetDateTime createdAt;
    private String customerName;
    private String customerPhone;
    private Long wineStoreId;
    private String wineStoreName;
    private BigDecimal totalAmount;
    private String statusName;
}

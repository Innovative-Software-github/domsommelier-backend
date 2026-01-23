package com.innovativesoftware.domsommelier_backend.order_management.order.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderHistoryDto {
    private UUID id;
    private OffsetDateTime date;
    private BigDecimal totalAmount;
    private String statusName;
    private String previewText;
}
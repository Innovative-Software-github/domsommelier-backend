package com.innovativesoftware.domsommelier_backend.admin_management.customer.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminCustomerOrderDto {
    private UUID id;
    private OffsetDateTime createdAt;
    private String statusName;
    private BigDecimal totalAmount;
    private String wineStoreName;
}

package com.innovativesoftware.domsommelier_backend.admin_management.event_order.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminEventOrderListDto {
    private UUID id;
    private OffsetDateTime createdAt;
    private String name;
    private String phone;
    private String statusName;
}

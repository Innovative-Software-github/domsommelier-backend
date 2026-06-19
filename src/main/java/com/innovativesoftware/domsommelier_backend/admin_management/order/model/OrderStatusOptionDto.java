package com.innovativesoftware.domsommelier_backend.admin_management.order.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderStatusOptionDto {
    private String name;
    private String label;
}

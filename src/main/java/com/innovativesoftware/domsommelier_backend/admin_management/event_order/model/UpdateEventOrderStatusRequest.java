package com.innovativesoftware.domsommelier_backend.admin_management.event_order.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateEventOrderStatusRequest {
    @NotBlank
    private String status;
}

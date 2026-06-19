package com.innovativesoftware.domsommelier_backend.admin_management.order.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotBlank
    private String status;
}

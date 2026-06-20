package com.innovativesoftware.domsommelier_backend.admin_management.customer.model;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import lombok.Data;

@Data
public class AdminCustomerFilterRequest {
    private String query;
    private Role role;
}

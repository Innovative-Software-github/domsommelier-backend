package com.innovativesoftware.domsommelier_backend.admin_management.customer.model;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdminCustomerListDto {
    private UUID id;
    private String displayName;
    private String email;
    private String phone;
    private Role role;
    private String defaultWineStoreName;
}

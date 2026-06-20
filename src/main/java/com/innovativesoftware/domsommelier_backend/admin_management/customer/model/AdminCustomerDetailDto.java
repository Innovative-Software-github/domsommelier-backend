package com.innovativesoftware.domsommelier_backend.admin_management.customer.model;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdminCustomerDetailDto {
    private UUID id;
    private String firstName;
    private String secondName;
    private String middleName;
    private String displayName;
    private String email;
    private String phone;
    private Role role;
    private Long defaultWineStoreId;
    private String defaultWineStoreName;
}

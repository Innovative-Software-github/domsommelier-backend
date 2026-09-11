package com.innovativesoftware.domsommelier_backend.admin_management.customer.model;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
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
    /** Личная скидка в процентах, 0 — скидки нет. */
    private Integer discountPercent;
    /** Основание для скидки — только для админки, клиенту не отдаётся. */
    private String discountComment;
    private OffsetDateTime discountUpdatedAt;
}

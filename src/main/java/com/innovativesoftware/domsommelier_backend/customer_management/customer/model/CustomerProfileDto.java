package com.innovativesoftware.domsommelier_backend.customer_management.customer.model;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerProfileDto {
    private UUID id;
    private String firstName;
    private String secondName;
    private String middleName;
    private String email;
    private String phone;
    private Role role;
    /** Личная скидка в процентах, 0 — скидки нет. */
    private Integer discountPercent;
}

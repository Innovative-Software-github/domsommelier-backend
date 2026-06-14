package com.innovativesoftware.domsommelier_backend.customer_management.customer.model;

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
}
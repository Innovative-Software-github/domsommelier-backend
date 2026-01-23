package com.innovativesoftware.domsommelier_backend.customer_management.customer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationData implements Serializable {
    private String code;
    private int attempts;
    private OffsetDateTime canResendAt;
    private OffsetDateTime createdAt;
}
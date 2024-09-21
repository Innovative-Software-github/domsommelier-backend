package com.innovativesoftware.domsommelier_backend.user.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class CustomerTopPurchasesDTO implements ComplexDTO, Serializable {
    private UUID customerId;
    private UUID productId;
    private Integer orderedCount;
}

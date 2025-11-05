package com.innovativesoftware.domsommelier_backend.product_management.store.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WineStorePoint {
    private Double longitude;
    private Double latitude;
}
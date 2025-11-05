package com.innovativesoftware.domsommelier_backend.product_management.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoordinatesDto {
    private Double longitude;
    private Double latitude;
}
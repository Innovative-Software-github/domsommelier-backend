package com.innovativesoftware.domsommelier_backend.product_management.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WineStoreFilterDto {
    private String city;
    private String district;
    private Double minLongitude;
    private Double maxLongitude;
    private Double minLatitude;
    private Double maxLatitude;
}
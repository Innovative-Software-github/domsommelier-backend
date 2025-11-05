package com.innovativesoftware.domsommelier_backend.product_management.store.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WineStoreResponseDto {
    private Long id;
//    private Double longitude;
//    private Double latitude;
    private CoordinatesDto location;
    private String city;
    private String district;
}

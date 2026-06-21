package com.innovativesoftware.domsommelier_backend.geo_management.city.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {
    private Long id;
    private String slug;
    private String name;
    private boolean active;
    private int sortOrder;
}

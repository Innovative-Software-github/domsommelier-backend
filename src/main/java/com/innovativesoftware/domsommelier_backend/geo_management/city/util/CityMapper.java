package com.innovativesoftware.domsommelier_backend.geo_management.city.util;

import com.innovativesoftware.domsommelier_backend.geo_management.city.entity.City;
import com.innovativesoftware.domsommelier_backend.geo_management.city.model.CityDto;

public final class CityMapper {

    private CityMapper() {
    }

    public static CityDto toDto(City city) {
        return CityDto.builder()
                .id(city.getId())
                .slug(city.getSlug())
                .name(city.getName())
                .active(city.isActive())
                .sortOrder(city.getSortOrder())
                .build();
    }
}

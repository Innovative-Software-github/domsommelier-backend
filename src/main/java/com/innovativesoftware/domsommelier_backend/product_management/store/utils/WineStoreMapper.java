package com.innovativesoftware.domsommelier_backend.product_management.store.utils;

import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStorePoint;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.CoordinatesDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreResponseDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface WineStoreMapper {
    WineStoreResponseDto toResponseDto(WineStore wineStore);

//    @Mapping(target = "longitude", source = "location.latitude")
//    @Mapping(target = "latitude", source = "location.longitude")
    CoordinatesDto toCoordinatesDto(WineStorePoint location);
}

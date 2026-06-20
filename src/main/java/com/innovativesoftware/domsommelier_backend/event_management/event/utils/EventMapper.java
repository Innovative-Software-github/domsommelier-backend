package com.innovativesoftware.domsommelier_backend.event_management.event.utils;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    private EventMapper() {
    }

    public static EventDTO toDto(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setType(event.getType());
        dto.setPrice(event.getPrice());
        dto.setDatetime(event.getDatetime());
        dto.setTitle(event.getTitle());
        dto.setSmallCover(event.getSmallCover());
        dto.setLargeCover(event.getLargeCover());
        dto.setCity(event.getCity());
        dto.setAddress(event.getAddress());
        dto.setDescription(event.getDescription());
        dto.setRegistrationLink(event.getRegistrationLink());
        dto.setWineStoreId(resolveWineStoreId(event));
        return dto;
    }

    public static void applyScalars(Event event, EventDTO dto) {
        event.setType(dto.getType());
        event.setPrice(dto.getPrice());
        event.setDatetime(dto.getDatetime());
        event.setTitle(dto.getTitle());
        event.setSmallCover(dto.getSmallCover());
        event.setLargeCover(dto.getLargeCover());
        event.setDescription(dto.getDescription());
        event.setRegistrationLink(dto.getRegistrationLink());
    }

    public static void applyWineStoreLocation(Event event, WineStore wineStore) {
        event.setWineStore(wineStore);
        event.setCity(wineStore.getCity());
        event.setAddress(wineStore.getAddress());
    }

    public static EventFullDTO toFullDto(Event event, String smallCoverUrl, String largeCoverUrl) {
        return EventFullDTO.builder()
                .id(event.getId().toString())
                .type(event.getType())
                .price(event.getPrice())
                .dateTime(formatToIsoWithMillisZ(event.getDatetime()))
                .title(event.getTitle())
                .smallCover(smallCoverUrl)
                .largeCover(largeCoverUrl)
                .city(event.getCity())
                .address(event.getAddress())
                .description(event.getDescription())
                .registrationLink(event.getRegistrationLink())
                .wineStoreId(resolveWineStoreId(event))
                .wineStoreName(resolveWineStoreName(event))
                .build();
    }

    public static EventListDTO toListDto(Event event, String smallCoverUrl) {
        return EventListDTO.builder()
                .id(event.getId().toString())
                .type(event.getType())
                .price(event.getPrice())
                .dateTime(formatToIsoWithMillisZ(event.getDatetime()))
                .title(event.getTitle())
                .smallCover(smallCoverUrl)
                .city(event.getCity())
                .wineStoreId(resolveWineStoreId(event))
                .wineStoreName(resolveWineStoreName(event))
                .build();
    }

    private static Long resolveWineStoreId(Event event) {
        WineStore wineStore = event.getWineStore();
        return wineStore != null ? wineStore.getId() : null;
    }

    private static String resolveWineStoreName(Event event) {
        WineStore wineStore = event.getWineStore();
        return wineStore != null ? wineStore.getName() : null;
    }

    private static String formatToIsoWithMillisZ(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_INSTANT);
    }
}

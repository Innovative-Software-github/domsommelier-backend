package com.innovativesoftware.domsommelier_backend.event_management.event.utils;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import org.springframework.beans.BeanUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class EventMapper {
    public static EventDTO toDto(Event event) {
        EventDTO dto = new EventDTO();
        BeanUtils.copyProperties(event, dto);
        return dto;
    }

    public static Event toEntity(EventDTO dto) {
        Event event = new Event();
        BeanUtils.copyProperties(dto, event);
        return event;
    }

    public static void updateEventFromDto(EventDTO dto, Event event) {
        event.setType(dto.getType());
        event.setPrice(dto.getPrice());
        event.setDatetime(dto.getDatetime());
        event.setTitle(dto.getTitle());
        event.setSmallCover(dto.getSmallCover());
        event.setLargeCover(dto.getLargeCover());
        event.setCity(dto.getCity());
        event.setAddress(dto.getAddress());
        event.setWineryIndex(dto.getWineryIndex());
        event.setDescription(dto.getDescription());
        event.setRegistrationLink(dto.getRegistrationLink());
    }

    // Для full
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
                .wineryIndex(event.getWineryIndex())
                .description(event.getDescription())
                .registrationLink(event.getRegistrationLink())
                .build();
    }

    // Для списка (короткое)
    public static EventListDTO toListDto(Event event, String smallCoverUrl) {
        return EventListDTO.builder()
                .id(event.getId().toString())
                .type(event.getType())
                .price(event.getPrice())
                .dateTime(formatToIsoWithMillisZ(event.getDatetime()))
                .title(event.getTitle())
                .smallCover(smallCoverUrl)
                .build();
    }

    private static String formatToIsoWithMillisZ(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_INSTANT);  // 2025-07-11T20:00:00.000Z
    }

    private static LocalDateTime combineDateAndTime(LocalDate date, LocalTime time) {
        return date.atTime(time);
    }
}

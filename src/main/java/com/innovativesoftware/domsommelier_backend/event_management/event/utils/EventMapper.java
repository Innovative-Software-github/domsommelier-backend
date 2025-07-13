package com.innovativesoftware.domsommelier_backend.event_management.event.utils;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

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
        event.setDate(dto.getDate());
        event.setTime(dto.getTime());
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
                .dateTime(makeDateTimeIso(event.getDate(), event.getTime()))
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
                .dateTime(makeDateTimeIso(event.getDate(), event.getTime()))
                .title(event.getTitle())
                .smallCover(smallCoverUrl)
                .build();
    }

    private static String makeDateTimeIso(LocalDate date, LocalTime time) {
        // Если в entity LocalDate+LocalTime: собираем в OffsetDateTime (или ZonedDateTime, если нужен часовой пояс)
        OffsetDateTime dateTime = date.atTime(time).atOffset(ZoneId.systemDefault().getRules().getOffset(java.time.Instant.now()));
        return dateTime.toString(); // ISO 8601
    }
}

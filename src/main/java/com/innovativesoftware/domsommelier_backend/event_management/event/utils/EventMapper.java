package com.innovativesoftware.domsommelier_backend.event_management.event.utils;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import org.springframework.beans.BeanUtils;

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
}

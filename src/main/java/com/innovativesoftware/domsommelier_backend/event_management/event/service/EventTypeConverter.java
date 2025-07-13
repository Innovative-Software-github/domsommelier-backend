package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventTypeConverter implements Converter<String, EventType> {
    @Override
    public EventType convert(String value) {
        return EventType.fromString(value)
                .orElseThrow(() -> new IllegalArgumentException("Unknown event type: " + value));
    }
}

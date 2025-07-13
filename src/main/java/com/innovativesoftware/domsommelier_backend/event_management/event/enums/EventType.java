package com.innovativesoftware.domsommelier_backend.event_management.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@AllArgsConstructor
@Getter
public enum EventType {
    WINE_CASINO("Винное казино"),
    DEGUSTATION("Дегустация");

    private final String displayName;

    public static Optional<EventType> fromString(String value) {
        for (EventType t : EventType.values()) {
            if (t.displayName.equalsIgnoreCase(value.trim())) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }
}

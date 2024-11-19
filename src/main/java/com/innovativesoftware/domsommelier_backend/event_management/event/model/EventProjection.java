package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface EventProjection {
    UUID getId();
    String getDescription();
    OffsetDateTime getStartedAt();
    OffsetDateTime getFinishedAt();
}

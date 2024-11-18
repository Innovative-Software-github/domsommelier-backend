package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import java.util.UUID;

public interface EventWithFileProjection extends EventProjection {
    UUID getEventFileId();
    String getBucket();
    String getFileName();
    String getEventFileDescription();
}

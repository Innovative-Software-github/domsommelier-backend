package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EventPhotoDTO {
    private UUID id;
    private UUID eventId;
    private String name;
    private String description;
    private String bucket;
    private String url; // ссылка на файл (можно сделать get-просмотр через контроллер)
}

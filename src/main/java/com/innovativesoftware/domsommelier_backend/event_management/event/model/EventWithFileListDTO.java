package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import com.innovativesoftware.domsommelier_backend.file_management.model.EntityWithFiles;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
public class EventWithFileListDTO implements EntityWithFiles<FileDTO>, EventProjection {
    private UUID id;
    private String description;
    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private List<FileDTO> files;
}

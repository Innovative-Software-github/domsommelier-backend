package com.innovativesoftware.domsommelier_backend.news_management.model;

import com.innovativesoftware.domsommelier_backend.file_management.model.EntityWithFiles;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
@SuperBuilder
public class NewsWithFileListDTO implements EntityWithFiles<FileDTO>, NewsProjection {
    private UUID id;
    private String description;
    private String title;
    private String reference;
    private List<FileDTO> files;
}

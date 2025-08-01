package com.innovativesoftware.domsommelier_backend.file_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Accessors(chain = true)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class FileDTO implements FileProjection {
    private UUID id;
    private String bucket;
    private String name;
    private String description;
    private String url;
}

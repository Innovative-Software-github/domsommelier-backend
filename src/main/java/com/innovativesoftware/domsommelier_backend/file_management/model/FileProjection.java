package com.innovativesoftware.domsommelier_backend.file_management.model;

import java.util.UUID;

public interface FileProjection {
    UUID getId();
    String getBucket();
    String getName();
    String getDescription();
}

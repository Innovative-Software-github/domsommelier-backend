package com.innovativesoftware.domsommelier_backend.news_management.model;

import java.util.UUID;

public interface NewsWithFileProjection extends NewsProjection {
    UUID getNewsFileId();
    String getBucket();
    String getFileName();
    String getNewFileDescription();
}

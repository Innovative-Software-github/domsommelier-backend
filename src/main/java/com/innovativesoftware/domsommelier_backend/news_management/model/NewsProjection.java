package com.innovativesoftware.domsommelier_backend.news_management.model;

import java.util.UUID;

public interface NewsProjection {
    UUID getId();
    String getDescription();
    String getTitle();
}

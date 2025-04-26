package com.innovativesoftware.domsommelier_backend.news_management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class FileRequestDto {
    private String bucket;
    private String name;
    private String description;
}

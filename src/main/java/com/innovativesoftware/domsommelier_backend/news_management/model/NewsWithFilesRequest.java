package com.innovativesoftware.domsommelier_backend.news_management.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class NewsWithFilesRequest {
    private String id;
    private String description;
    private String title;
    private String reference;
    private List<FileRequestDto> files;
}

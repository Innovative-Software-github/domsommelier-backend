package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventListDTO {
    private String id;
    private String type;
    private Integer price;
    private String dateTime;         // ISO 8601
    private String title;
    private String smallCover;
    private String city;
    private Long wineStoreId;
    private String wineStoreName;
}

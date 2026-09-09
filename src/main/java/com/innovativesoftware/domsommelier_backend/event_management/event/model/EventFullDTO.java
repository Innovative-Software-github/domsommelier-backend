package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EventFullDTO {
    private String id;
    private String type;
    private Integer price;
    private String dateTime;         // ISO 8601
    private String title;
    private String smallCover;
    private String largeCover;
    private String city;
    private String address;
    private String description;
    private String registrationLink;
    private String about;
    private String howItGoes;
    private List<FaqItemDto> faq;
    private Long wineStoreId;
    private String wineStoreName;
}

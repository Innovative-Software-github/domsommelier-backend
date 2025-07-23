package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventDTO {
    private UUID id;
    private String type;
    private Integer price;
    private OffsetDateTime datetime;
    private String title;
    private String smallCover;
    private String largeCover;
    private String city;
    private String address;
    private String wineryIndex;
    private String description;
    private String registrationLink;
}

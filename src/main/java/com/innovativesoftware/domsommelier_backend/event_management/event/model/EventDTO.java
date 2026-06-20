package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import com.innovativesoftware.domsommelier_backend.annotations.EventTypeSubset;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventDTO {
    private UUID id;
    @Schema(description = "Тип мероприятия (wineCasino или degustation)", example = "wineCasino")
    @EventTypeSubset(message = "Недопустимый тип мероприятия. Допустимые значения: wineCasino, degustation.")
    private String type;
    private Integer price;
    @Schema(description = "Дата и время мероприятия в формате ISO 8601", example = "2025-07-11T20:00:00.000Z")
    private OffsetDateTime datetime;
    private String title;
    private String smallCover;
    private String largeCover;
    private String city;
    private String address;
    private String description;
    private String registrationLink;
    @NotNull(message = "Укажите винотеку")
    private Long wineStoreId;
}

package com.innovativesoftware.domsommelier_backend.event_management.event.model;

import com.innovativesoftware.domsommelier_backend.annotations.ConsistentDatesAndPrices;
import com.innovativesoftware.domsommelier_backend.annotations.EventTypeSubset;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConsistentDatesAndPrices
public class EventFilterRequest {

    @Schema(description = "Дата начала (формат: ISO 8601, например: 2025-07-11T20:00:00.000Z)",
            example = "2025-07-11T20:00:00.000Z", type = "string", format = "date-time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
    private OffsetDateTime dateStart;

    @Schema(description = "Дата конца (формат: ISO 8601, например: 2025-07-15T20:00:00.000Z)",
            example = "2025-07-15T20:00:00.000Z", type = "string", format = "date-time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]")
    private OffsetDateTime dateEnd;

    @Schema(description = "Минимальная цена (например: 1000)", example = "1000", type = "integer")
    private Integer priceMin;

    @Schema(description = "Максимальная цена (например: 5000)", example = "5000", type = "integer")
    private Integer priceMax;

    @Schema(description = "Тип мероприятия (wineCasino или degustation)", example = "wineCasino", allowableValues = {"wineCasino", "degustation"})
    @EventTypeSubset(message = "Недопустимый тип мероприятия. Допустимые значения: wineCasino, degustation")
    private String type;

    @Schema(description = "ID винотеки", example = "1")
    private Long wineStoreId;

    @Schema(description = "Город (slug винотеки) — мероприятия по всем винотекам города", example = "moscow")
    private String city;

    @Schema(description = "Номер страницы (с 0)", example = "0")
    private Integer page = 0;

    @Schema(description = "Размер страницы", example = "10")
    private Integer size = 10;
}


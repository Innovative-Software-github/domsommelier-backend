package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventType;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Управление мероприятиями магазина (CRUD)")
public class EventController {

    private final EventService eventService;

    /*
    @Operation(summary = "Получить список всех мероприятий")
    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.getAllEvents();
    }*/

    /*
    @Operation(summary = "Получить мероприятие по id")
    @GetMapping("/{id}")
    public ResponseEntity<EventFullDTO> getEventById(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }*/

    @Operation(summary = "Создать новое мероприятие")
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @Parameter(description = "Данные мероприятия") @RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @Operation(summary = "Обновить мероприятие по id")
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @Parameter(description = "ID мероприятия", required = true) @PathVariable UUID id,
            @Parameter(description = "Новые данные мероприятия") @RequestBody EventDTO eventDTO
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDTO));
    }

    @Operation(summary = "Удалить мероприятие по id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID мероприятия", required = true) @PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить страницу событий (короткое представление, с пагинацией)")
    @GetMapping
    public Page<EventListDTO> getEvents(
            @Parameter(description = "Номер страницы (с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getEventsPage(page, size);
    }

    @Operation(summary = "Получить подробную информацию о событии по ID")
    @GetMapping("/{id}")
    public EventFullDTO getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }

    @Operation(summary = "Получить страницу событий с фильтрами и пагинацией")
    @GetMapping("/filter")
    public Page<EventListDTO> getFilteredEvents(
            @Parameter(
                    description = "Дата начала (формат: YYYY-MM-DD, например: 2025-07-11)",
                    schema = @Schema(type = "string", example = "2025-07-11")
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,

            @Parameter(
                    description = "Дата конца (формат: YYYY-MM-DD, например: 2025-07-15)",
                    schema = @Schema(type = "string", example = "2025-07-15")
            )
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(
                    description = "Минимальная цена (например: 1000)",
                    schema = @Schema(type = "integer", example = "1000")
            )
            @RequestParam(required = false) Integer priceMin,

            @Parameter(
                    description = "Максимальная цена (например: 5000)",
                    schema = @Schema(type = "integer", example = "5000")
            )
            @RequestParam(required = false) Integer priceMax,

            @Parameter(
                    description = "Тип мероприятия (Винное казино или Дегустация)",
                    schema = @Schema(type = "string", allowableValues = {"Винное казино", "Дегустация"}, example = "Дегустация")
            )
            @RequestParam(required = false) EventType type,

            @Parameter(
                    description = "Номер страницы (с 0)",
                    schema = @Schema(type = "integer", example = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = "Размер страницы",
                    schema = @Schema(type = "integer", example = "10")
            )
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getFilteredEvents(dateStart, endDate, priceMin, priceMax, type, page, size);
    }

}

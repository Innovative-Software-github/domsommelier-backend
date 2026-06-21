package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventType;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFilterRequest;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@Validated
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
    @RequiresAdmin
    public ResponseEntity<EventDTO> createEvent(
            @Parameter(description = "Данные мероприятия") @RequestBody @Valid EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @Operation(summary = "Обновить мероприятие по id")
    @PutMapping("/{id}")
    @RequiresAdmin
    public ResponseEntity<EventDTO> updateEvent(
            @Parameter(description = "ID мероприятия", required = true) @PathVariable UUID id,
            @Parameter(description = "Новые данные мероприятия") @RequestBody @Valid EventDTO eventDTO
    ) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDTO));
    }

    @Operation(summary = "Удалить мероприятие по id")
    @DeleteMapping("/{id}")
    @RequiresAdmin
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID мероприятия", required = true) @PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

//    @Hidden
//    @Operation(summary = "Получить страницу событий (короткое представление, с пагинацией)")
//    @GetMapping
//    public Page<EventListDTO> getEvents(
//            @Parameter(description = "Номер страницы (с 0)") @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
//    ) {
//        return eventService.getEventsPage(page, size);
//    }

    @Operation(summary = "Получить подробную информацию о событии по ID")
    @GetMapping("/{id}")
    public EventFullDTO getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/filter")
    @Operation(summary = "Получить страницу событий с фильтрами и пагинацией")
    public Page<EventListDTO> getFilteredEvents(@Valid @ModelAttribute EventFilterRequest filter) {
        EventType eventType = filter.getType() == null ? null : EventType.fromString(filter.getType()).orElse(null);
        return eventService.getFilteredEvents(
                filter.getDateStart(),
                filter.getDateEnd(),
                filter.getPriceMin(),
                filter.getPriceMax(),
                eventType,
                filter.getWineStoreId(),
                filter.getCity(),
                filter.getPage(),
                filter.getSize()
        );
    }
}

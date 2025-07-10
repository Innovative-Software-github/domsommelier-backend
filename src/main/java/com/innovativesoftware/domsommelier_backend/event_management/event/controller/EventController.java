package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Управление мероприятиями магазина (CRUD)")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Получить список всех мероприятий")
    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @Operation(summary = "Получить мероприятие по id")
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(
            @Parameter(description = "ID мероприятия", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

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
}


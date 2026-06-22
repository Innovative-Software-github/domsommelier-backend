package com.innovativesoftware.domsommelier_backend.event_management.event_order.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event_order.model.CreateEventOrderRequest;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.service.EventOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/v1/event-orders")
@Tag(name = "Event Orders", description = "Заявки на проведение мероприятий")
public class EventOrderController {

    private final EventOrderService eventOrderService;

    @PostMapping
    @Operation(summary = "Создать заявку на проведение мероприятия")
    public ResponseEntity<UUID> create(@Valid @RequestBody CreateEventOrderRequest request) {
        UUID id = eventOrderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}

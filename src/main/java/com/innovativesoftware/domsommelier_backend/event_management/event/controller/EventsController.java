package com.innovativesoftware.domsommelier_backend.event_management.event.controller;

import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventWithFileListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventsController {
    @Autowired
    private EventService eventService;

    @GetMapping("")
    public List<EventWithFileListDTO> findAllEvents() {
        return eventService.findAllEvents();
    }
}


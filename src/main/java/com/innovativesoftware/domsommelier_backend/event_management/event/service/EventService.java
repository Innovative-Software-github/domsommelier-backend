package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventProjection;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<EventProjection> findAllEvents() {
        return eventRepository.findAllEvents();
    }
}

package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventWithFileListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventWithFileProjection;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<EventWithFileListDTO> findAllEvents() {
        List<EventWithFileProjection> eventsWithFile = eventRepository.findAllEvents();
        return eventsWithFile.stream().map(eventWithFile ->
                new EventWithFileListDTO()
                        .setId(eventWithFile.getId())
                        .setDescription(eventWithFile.getDescription())
                        .setStartedAt(eventWithFile.getStartedAt())
                        .setFinishedAt(eventWithFile.getFinishedAt())
                        .setFiles(
                                new ArrayList<>(List.of(
                                        new FileDTO()
                                                .setId(eventWithFile.getEventFileId())
                                                .setBucket(eventWithFile.getBucket())
                                                .setName(eventWithFile.getFileName())
                                                .setDescription(eventWithFile.getEventFileDescription()))))
                )
                .toList();
    }
}

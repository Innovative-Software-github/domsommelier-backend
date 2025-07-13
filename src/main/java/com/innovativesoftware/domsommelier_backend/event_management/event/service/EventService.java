package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventType;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.event_management.event.utils.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    /*public EventDTO getEventById(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        return EventMapper.toDto(event);
    }*/

    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = EventMapper.toEntity(eventDTO);
        event.setId(UUID.randomUUID());
        Event saved = eventRepository.save(event);
        return EventMapper.toDto(saved);
    }

    public EventDTO updateEvent(UUID id, EventDTO eventDTO) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        EventMapper.updateEventFromDto(eventDTO, event);
        Event updated = eventRepository.save(event);
        return EventMapper.toDto(updated);
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }

    public Page<EventListDTO> getEventsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending().and(Sort.by("time").descending()));
        Page<Event> eventPage = eventRepository.findAll(pageable);

        return eventPage.map(event ->
                EventMapper.toListDto(event, event.getSmallCover())
        );
    }

    public EventFullDTO getEventById(UUID id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        return EventMapper.toFullDto(
                event,
                event.getSmallCover(),
                event.getLargeCover()
        );
    }

    public Page<EventListDTO> getFilteredEvents(
            LocalDate dateStart, LocalDate endDate,
            Integer priceMin, Integer priceMax,
            EventType type, int page, int size
    ) {
        Specification<Event> spec = Specification.where(null);

        // Фильтр по дате
        if (dateStart != null && endDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("date"), dateStart, endDate));
        } else if (dateStart != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("date"), dateStart));
        } else if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("date"), endDate));
        }

        // Фильтр по цене
        if (priceMin != null && priceMax != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("price"), priceMin, priceMax));
        } else if (priceMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), priceMin));
        } else if (priceMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), priceMax));
        }

        // Фильтр по типу (enum или просто строка)
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type.getDisplayName()));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Event> eventPage = eventRepository.findAll(spec, pageable);

        return eventPage.map(event -> EventMapper.toListDto(event, event.getSmallCover()));
    }
}

package com.innovativesoftware.domsommelier_backend.event_management.event.service;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventType;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFullDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventListDTO;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.event_management.event.utils.EventMapper;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventPhotoOperationService eventPhotoOperationService;
    private final WineStoreRepository wineStoreRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        EventMapper.applyScalars(event, eventDTO);
        applyWineStore(event, eventDTO.getWineStoreId());
        Event saved = eventRepository.save(event);
        return EventMapper.toDto(saved);
    }

    @Transactional
    public EventDTO updateEvent(UUID id, EventDTO eventDTO) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found"));
        EventMapper.applyScalars(event, eventDTO);
        applyWineStore(event, eventDTO.getWineStoreId());
        Event updated = eventRepository.save(event);
        return EventMapper.toDto(updated);
    }

    @Transactional
    public void deleteEvent(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new NoSuchElementException("Event not found");
        }
        eventPhotoOperationService.deletePhotosByEventId(id);
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
            OffsetDateTime dateStart, OffsetDateTime endDate,
            Integer priceMin, Integer priceMax,
            EventType type, Long wineStoreId, int page, int size
    ) {
        Specification<Event> spec = Specification.where(null);

        if (dateStart != null && endDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("datetime"), dateStart, endDate));
        } else if (dateStart != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("datetime"), dateStart));
        } else if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("datetime"), endDate));
        }

        if (priceMin != null && priceMax != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("price"), priceMin, priceMax));
        } else if (priceMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), priceMin));
        } else if (priceMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), priceMax));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type.getDisplayName()));
        }

        if (wineStoreId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("wineStore").get("id"), wineStoreId));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("datetime").descending());
        Page<Event> eventPage = eventRepository.findAll(spec, pageable);

        return eventPage.map(event -> EventMapper.toListDto(event, event.getSmallCover()));
    }

    private void applyWineStore(Event event, Long wineStoreId) {
        WineStore wineStore = wineStoreRepository.findById(wineStoreId)
                .orElseThrow(() -> new EntityNotFoundException("Винотека не найдена: " + wineStoreId));
        EventMapper.applyWineStoreLocation(event, wineStore);
    }
}

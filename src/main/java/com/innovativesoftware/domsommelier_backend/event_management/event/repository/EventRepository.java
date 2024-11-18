package com.innovativesoftware.domsommelier_backend.event_management.event.repository;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("""
        select 
            event.id as id,
            event.description as description,
            event.startedAt as startedAt,
            event.finishedAt as finishedAt,
            event.eventPhoto.id as photoId
          from Event event
    """)
    List<EventProjection> findAllEvents();
}
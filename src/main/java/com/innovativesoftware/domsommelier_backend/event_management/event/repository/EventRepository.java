package com.innovativesoftware.domsommelier_backend.event_management.event.repository;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventWithFileProjection;
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
            eventPhoto.id as eventFileId,
            eventPhoto.bucket as bucket,
            eventPhoto.name as fileName,
            eventPhoto.description as eventFileDescription
          from Event event left join EventPhoto eventPhoto on event.id = eventPhoto.event.id
    """)
    List<EventWithFileProjection> findAllEvents();
}
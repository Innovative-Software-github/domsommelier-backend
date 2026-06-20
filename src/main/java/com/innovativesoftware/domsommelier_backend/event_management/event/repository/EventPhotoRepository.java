package com.innovativesoftware.domsommelier_backend.event_management.event.repository;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, UUID> {
    @Query("SELECT COUNT(e) > 0 FROM EventPhoto e WHERE e.url = ?1 AND e.event.id = ?2")
    boolean existsByUrlAndEventId(String url, UUID eventId);

    List<EventPhoto> findByEvent_Id(UUID eventId);
}
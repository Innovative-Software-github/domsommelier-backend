package com.innovativesoftware.domsommelier_backend.event_management.event.repository;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventPhotoRepository extends JpaRepository<EventPhoto, UUID> {
}
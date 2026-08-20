package com.innovativesoftware.domsommelier_backend.event_management.event.repository;

import com.innovativesoftware.domsommelier_backend.event_management.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByType(String type);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    boolean existsByWineStore_Id(Long wineStoreId);

    long countByDatetimeAfter(OffsetDateTime dateTime);
}

package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description")
    private String description;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "size")
    private Integer size;

    @Column(name = "event_host")
    private String eventHost;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status")
    private EventStatus eventStatus;

    @OneToOne(mappedBy = "event")
    private EventPhoto eventPhoto;
}
package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

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
    private OffsetDateTime finished_at;

    @Column(name = "size")
    private Integer size;

    @Column(name = "event_host")
    private String eventHost;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private EventStatus eventStatus;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private EventPhoto eventPhoto;

}
package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "event_photo")
public class EventPhoto {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "eventPhoto")
    private List<Event> events;
}
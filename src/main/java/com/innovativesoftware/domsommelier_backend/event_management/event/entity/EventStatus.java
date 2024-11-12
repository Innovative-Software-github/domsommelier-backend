package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "event_status")
public class EventStatus {

    // TODO: to enum
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "eventStatus")
    private List<Event> events;
}
package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event_status")
public class EventStatus {

    // TODO: to enum
    @Id
    @Column(name = "name", nullable = false)
    private String name;

//    @OneToMany(mappedBy = "eventStatus")
//    private List<Event> events;
}
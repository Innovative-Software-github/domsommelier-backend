package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "event_photo")
public class EventPhoto extends File {
    @OneToMany(mappedBy = "eventPhoto")
    private List<Event> events;
}
package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Entity
@NoArgsConstructor
@SuperBuilder
@Table(name = "event_photo")
public class EventPhoto extends File {
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}
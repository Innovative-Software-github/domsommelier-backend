package com.innovativesoftware.domsommelier_backend.file_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public class File {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    protected UUID id;

    @Column(name="bucket")
    protected String bucket;

    @Column(name= "name")
    protected String name;

    @Column(name = "description")
    protected String description;
}

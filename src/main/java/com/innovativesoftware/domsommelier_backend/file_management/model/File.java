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
    public UUID id;

    @Column(name="bucket", nullable = false)
    public String bucket;

    @Column(name= "name", nullable = false)
    public String name;

    @Column(name = "description")
    public String description;
}

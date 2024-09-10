package com.innovativesoftware.domsommelier_backend.shared.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class File {
    @Id
    @Column(name = "link", nullable = false)
    private String link;

    @Column(name= "name")
    private String name;

    @Column(name = "description")
    private String description;
}

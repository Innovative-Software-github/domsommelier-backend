package com.innovativesoftware.domsommelier_backend.shared.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@IdClass(FileId.class)
public class File<TDomain> {
    @Id
    @Column(name="bucket")
    protected String bucket;

    @Id
    @Column(name= "name")
    protected String name;

    @Column(name = "description")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id")
    private TDomain domain;
}

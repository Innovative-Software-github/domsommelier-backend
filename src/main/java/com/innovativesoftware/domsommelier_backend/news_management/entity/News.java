package com.innovativesoftware.domsommelier_backend.news_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "news")
public class News {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description")
    private String description;

    @Column(name = "title")
    private String title;

    @Column(name = "reference")
    private String reference;

    @OneToOne(mappedBy = "aNews")
    private NewsPhoto newsPhoto;
}

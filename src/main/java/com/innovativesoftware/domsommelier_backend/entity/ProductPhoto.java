package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_photo")
public class ProductPhoto {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
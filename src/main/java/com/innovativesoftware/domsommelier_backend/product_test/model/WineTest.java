package com.innovativesoftware.domsommelier_backend.product_test.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wine_test")
public class WineTest {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private ProductTest product;

    @Column(name = "color")
    private String color;
}
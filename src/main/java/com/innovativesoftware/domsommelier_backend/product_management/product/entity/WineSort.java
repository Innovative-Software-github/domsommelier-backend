package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wine_sort")
public class WineSort {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "percent", nullable = false)
    private Integer percent;

    @ManyToMany(mappedBy = "wineSorts")
    private List<Wine> wines;
}
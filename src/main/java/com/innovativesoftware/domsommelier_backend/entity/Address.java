package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Internal;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@Getter
@Setter
public class Address {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "region")
    private String region;

    @Column(name = "city")
    private String city;

    @Column(name = "house")
    private String house;

    @Column(name = "apartment")
    private Integer apartment;

    @OneToMany(mappedBy = "address")
    private List<Order> orders;
}
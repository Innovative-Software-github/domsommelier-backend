package com.innovativesoftware.domsommelier_backend.customer_management.customer.entity;

import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "addresses")
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
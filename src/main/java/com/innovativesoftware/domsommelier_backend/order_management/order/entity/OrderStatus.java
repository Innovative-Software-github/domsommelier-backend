package com.innovativesoftware.domsommelier_backend.order_management.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "order_status")
public class OrderStatus {

    // TODO: to enum
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "orderStatus")
    private List<Order> orders;
}
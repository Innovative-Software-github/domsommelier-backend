package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
}

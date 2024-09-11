package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "promo_use")
@IdClass(PromoUseId.class)
public class PromoUse {
    @Id
    @ManyToOne
    @JoinColumn(name = "promo_id", nullable = false)
    private Promo promo;

    @Id
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Id
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "is_used")
    private Boolean isUsed;
}
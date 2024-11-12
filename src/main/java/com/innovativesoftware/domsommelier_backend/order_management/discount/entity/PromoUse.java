package com.innovativesoftware.domsommelier_backend.order_management.discount.entity;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
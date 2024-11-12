package com.innovativesoftware.domsommelier_backend.order_management.discount.entity;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PromoUseId implements Serializable {
    private Promo promo;
    private Customer customer;
    private Order order;
}
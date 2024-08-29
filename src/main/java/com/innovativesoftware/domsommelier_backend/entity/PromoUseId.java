package com.innovativesoftware.domsommelier_backend.entity;

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
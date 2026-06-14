package com.innovativesoftware.domsommelier_backend.order_management.basket.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CheckoutRequestDto {
    private String customerName;
    private String customerPhone;
    private LocalDate pickupDate;
    private String paymentMethod;
}
